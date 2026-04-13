package com.hostel.service.impl;

import com.hostel.exception.InvalidOperationException;
import com.hostel.exception.ResourceNotFoundException;
import com.hostel.model.Allocation;
import com.hostel.model.Room;
import com.hostel.model.User;
import com.hostel.model.enums.AllocationStatus;
import com.hostel.model.enums.Role;
import com.hostel.model.enums.RoomType;
import com.hostel.repository.AllocationRepository;
import com.hostel.repository.RoomRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for AllocationServiceImpl.
 * Tests business rules: double-booking prevention, status transitions,
 * room occupancy updates, and vacancy checks.
 */
@ExtendWith(MockitoExtension.class)
class AllocationServiceImplTest {

    @Mock
    private AllocationRepository allocationRepository;

    @Mock
    private RoomRepository roomRepository;

    @InjectMocks
    private AllocationServiceImpl allocationService;

    private User student;
    private User warden;
    private Room room;

    @BeforeEach
    void setUp() {
        student = new User("student1", "pass", "Test Student", "student@test.com", "1234567890", Role.STUDENT);
        student.setId(1L);

        warden = new User("warden1", "pass", "Test Warden", "warden@test.com", "0987654321", Role.WARDEN);
        warden.setId(2L);

        room = new Room("A101", RoomType.SINGLE, 1, 5000.0, 1, null);
        room.setId(1L);
        room.setCurrentOccupancy(0);
        room.setAvailable(true);
    }

    // ═══════════════ Apply for Room ═══════════════

    @Test
    @DisplayName("Apply for room — success when no active allocation exists")
    void applyForRoom_success() {
        when(allocationRepository.hasActiveAllocation(student.getId())).thenReturn(false);
        when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));
        when(allocationRepository.save(any(Allocation.class))).thenAnswer(i -> i.getArgument(0));

        Allocation allocation = allocationService.applyForRoom(student, room.getId());

        assertNotNull(allocation);
        assertEquals(AllocationStatus.PENDING, allocation.getStatus());
        assertEquals(student, allocation.getStudent());
        assertEquals(room, allocation.getRoom());
    }

    @Test
    @DisplayName("Apply for room — should throw when student has active allocation (double-booking prevention)")
    void applyForRoom_doubleBooking_shouldThrow() {
        when(allocationRepository.hasActiveAllocation(student.getId())).thenReturn(true);

        assertThrows(InvalidOperationException.class,
                () -> allocationService.applyForRoom(student, room.getId()));
    }

    @Test
    @DisplayName("Apply for room — should throw when room is full")
    void applyForRoom_roomFull_shouldThrow() {
        room.setCurrentOccupancy(1); // capacity is 1, so it's full
        when(allocationRepository.hasActiveAllocation(student.getId())).thenReturn(false);
        when(roomRepository.findById(room.getId())).thenReturn(Optional.of(room));

        assertThrows(InvalidOperationException.class,
                () -> allocationService.applyForRoom(student, room.getId()));
    }

    @Test
    @DisplayName("Apply for room — should throw when room not found")
    void applyForRoom_roomNotFound_shouldThrow() {
        when(allocationRepository.hasActiveAllocation(student.getId())).thenReturn(false);
        when(roomRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> allocationService.applyForRoom(student, 999L));
    }

    // ═══════════════ Approve Allocation ═══════════════

    @Test
    @DisplayName("Approve allocation — success when PENDING")
    void approveAllocation_success() {
        Allocation allocation = new Allocation(student, room);
        allocation.setId(1L);
        allocation.setStatus(AllocationStatus.PENDING);

        when(allocationRepository.findById(1L)).thenReturn(Optional.of(allocation));
        when(allocationRepository.save(any(Allocation.class))).thenAnswer(i -> i.getArgument(0));

        Allocation result = allocationService.approveAllocation(1L, warden);

        assertEquals(AllocationStatus.APPROVED, result.getStatus());
        assertEquals(warden, result.getApprovedBy());
        assertNotNull(result.getApprovalDate());
    }

    @Test
    @DisplayName("Approve allocation — should throw when not PENDING")
    void approveAllocation_notPending_shouldThrow() {
        Allocation allocation = new Allocation(student, room);
        allocation.setId(1L);
        allocation.setStatus(AllocationStatus.CONFIRMED);

        when(allocationRepository.findById(1L)).thenReturn(Optional.of(allocation));

        assertThrows(InvalidOperationException.class,
                () -> allocationService.approveAllocation(1L, warden));
    }

    // ═══════════════ Reject Allocation ═══════════════

    @Test
    @DisplayName("Reject allocation — success when PENDING")
    void rejectAllocation_success() {
        Allocation allocation = new Allocation(student, room);
        allocation.setId(1L);
        allocation.setStatus(AllocationStatus.PENDING);

        when(allocationRepository.findById(1L)).thenReturn(Optional.of(allocation));
        when(allocationRepository.save(any(Allocation.class))).thenAnswer(i -> i.getArgument(0));

        Allocation result = allocationService.rejectAllocation(1L, warden, "Room not suitable");

        assertEquals(AllocationStatus.REJECTED, result.getStatus());
        assertEquals("Room not suitable", result.getRemarks());
    }

    // ═══════════════ Confirm Allocation ═══════════════

    @Test
    @DisplayName("Confirm allocation — should increment room occupancy and set status")
    void confirmAllocation_success() {
        Allocation allocation = new Allocation(student, room);
        allocation.setId(1L);
        allocation.setStatus(AllocationStatus.APPROVED);

        when(allocationRepository.findById(1L)).thenReturn(Optional.of(allocation));
        when(roomRepository.save(any(Room.class))).thenAnswer(i -> i.getArgument(0));
        when(allocationRepository.save(any(Allocation.class))).thenAnswer(i -> i.getArgument(0));

        Allocation result = allocationService.confirmAllocation(1L);

        assertEquals(AllocationStatus.CONFIRMED, result.getStatus());
        assertEquals(1, room.getCurrentOccupancy());
        assertNotNull(result.getCheckInDate());
    }

    @Test
    @DisplayName("Confirm allocation — should mark room unavailable when full")
    void confirmAllocation_roomBecomesFull() {
        // capacity=1, occupancy starts at 0
        Allocation allocation = new Allocation(student, room);
        allocation.setId(1L);
        allocation.setStatus(AllocationStatus.APPROVED);

        when(allocationRepository.findById(1L)).thenReturn(Optional.of(allocation));
        when(roomRepository.save(any(Room.class))).thenAnswer(i -> i.getArgument(0));
        when(allocationRepository.save(any(Allocation.class))).thenAnswer(i -> i.getArgument(0));

        allocationService.confirmAllocation(1L);

        assertFalse(room.isAvailable(), "Room should be marked unavailable when full");
    }

    @Test
    @DisplayName("Confirm allocation — should throw when not APPROVED")
    void confirmAllocation_notApproved_shouldThrow() {
        Allocation allocation = new Allocation(student, room);
        allocation.setId(1L);
        allocation.setStatus(AllocationStatus.PENDING);

        when(allocationRepository.findById(1L)).thenReturn(Optional.of(allocation));

        assertThrows(InvalidOperationException.class,
                () -> allocationService.confirmAllocation(1L));
    }

    // ═══════════════ Vacate Room ═══════════════

    @Test
    @DisplayName("Vacate room — should decrement occupancy and set VACATED status")
    void vacateRoom_success() {
        room.setCurrentOccupancy(1);
        Allocation allocation = new Allocation(student, room);
        allocation.setId(1L);
        allocation.setStatus(AllocationStatus.CONFIRMED);

        when(allocationRepository.findById(1L)).thenReturn(Optional.of(allocation));
        when(roomRepository.save(any(Room.class))).thenAnswer(i -> i.getArgument(0));
        when(allocationRepository.save(any(Allocation.class))).thenAnswer(i -> i.getArgument(0));

        Allocation result = allocationService.vacateRoom(1L);

        assertEquals(AllocationStatus.VACATED, result.getStatus());
        assertEquals(0, room.getCurrentOccupancy());
        assertTrue(room.isAvailable());
        assertNotNull(result.getCheckOutDate());
    }

    @Test
    @DisplayName("Vacate room — should throw when not CONFIRMED")
    void vacateRoom_notConfirmed_shouldThrow() {
        Allocation allocation = new Allocation(student, room);
        allocation.setId(1L);
        allocation.setStatus(AllocationStatus.APPROVED);

        when(allocationRepository.findById(1L)).thenReturn(Optional.of(allocation));

        assertThrows(InvalidOperationException.class,
                () -> allocationService.vacateRoom(1L));
    }
}
