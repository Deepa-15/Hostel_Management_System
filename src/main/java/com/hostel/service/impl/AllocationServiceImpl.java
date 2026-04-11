package com.hostel.service.impl;

import com.hostel.exception.InvalidOperationException;
import com.hostel.exception.ResourceNotFoundException;
import com.hostel.model.Allocation;
import com.hostel.model.Room;
import com.hostel.model.User;
import com.hostel.model.enums.AllocationStatus;
import com.hostel.repository.AllocationRepository;
import com.hostel.repository.RoomRepository;
import com.hostel.service.AllocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Allocation service handles the room allocation lifecycle.
 * 
 * Key business rules:
 * - Prevents double booking (one active allocation per student)
 * - Checks room vacancy before allocation
 * - Updates room occupancy on confirm/vacate
 */
@Service
@Transactional
public class AllocationServiceImpl implements AllocationService {

    private final AllocationRepository allocationRepository;
    private final RoomRepository roomRepository;

    @Autowired
    public AllocationServiceImpl(AllocationRepository allocationRepository,
                                  RoomRepository roomRepository) {
        this.allocationRepository = allocationRepository;
        this.roomRepository = roomRepository;
    }

    @Override
    public Allocation applyForRoom(User student, Long roomId) {
        // Prevent double booking
        if (allocationRepository.hasActiveAllocation(student.getId())) {
            throw new InvalidOperationException(
                "You already have an active room allocation. Please vacate your current room before applying for a new one.");
        }

        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResourceNotFoundException("Room", roomId));

        // Check room availability
        if (!room.hasVacancy()) {
            throw new InvalidOperationException(
                "Room " + room.getRoomNumber() + " is fully occupied. Please choose another room.");
        }

        Allocation allocation = new Allocation(student, room);
        return allocationRepository.save(allocation);
    }

    @Override
    public Allocation approveAllocation(Long allocationId, User warden) {
        Allocation allocation = allocationRepository.findById(allocationId)
                .orElseThrow(() -> new ResourceNotFoundException("Allocation", allocationId));

        if (allocation.getStatus() != AllocationStatus.PENDING) {
            throw new InvalidOperationException(
                "Allocation is not in PENDING status. Current status: " + allocation.getStatus());
        }

        // Verify room still has vacancy
        Room room = allocation.getRoom();
        if (!room.hasVacancy()) {
            throw new InvalidOperationException("Room " + room.getRoomNumber() + " is now full.");
        }

        allocation.setStatus(AllocationStatus.APPROVED);
        allocation.setApprovedBy(warden);
        allocation.setApprovalDate(LocalDateTime.now());

        return allocationRepository.save(allocation);
    }

    @Override
    public Allocation rejectAllocation(Long allocationId, User warden, String remarks) {
        Allocation allocation = allocationRepository.findById(allocationId)
                .orElseThrow(() -> new ResourceNotFoundException("Allocation", allocationId));

        if (allocation.getStatus() != AllocationStatus.PENDING) {
            throw new InvalidOperationException(
                "Allocation is not in PENDING status. Current status: " + allocation.getStatus());
        }

        allocation.setStatus(AllocationStatus.REJECTED);
        allocation.setApprovedBy(warden);
        allocation.setApprovalDate(LocalDateTime.now());
        allocation.setRemarks(remarks);

        return allocationRepository.save(allocation);
    }

    @Override
    public Allocation confirmAllocation(Long allocationId) {
        Allocation allocation = allocationRepository.findById(allocationId)
                .orElseThrow(() -> new ResourceNotFoundException("Allocation", allocationId));

        if (allocation.getStatus() != AllocationStatus.APPROVED) {
            throw new InvalidOperationException(
                "Allocation must be APPROVED before confirmation. Current status: " + allocation.getStatus());
        }

        // Update room occupancy
        Room room = allocation.getRoom();
        room.setCurrentOccupancy(room.getCurrentOccupancy() + 1);
        if (room.getCurrentOccupancy() >= room.getCapacity()) {
            room.setAvailable(false);
        }
        roomRepository.save(room);

        allocation.setStatus(AllocationStatus.CONFIRMED);
        allocation.setCheckInDate(LocalDate.now());

        return allocationRepository.save(allocation);
    }

    @Override
    public Allocation vacateRoom(Long allocationId) {
        Allocation allocation = allocationRepository.findById(allocationId)
                .orElseThrow(() -> new ResourceNotFoundException("Allocation", allocationId));

        if (allocation.getStatus() != AllocationStatus.CONFIRMED) {
            throw new InvalidOperationException(
                "Only CONFIRMED allocations can be vacated. Current status: " + allocation.getStatus());
        }

        // Update room occupancy
        Room room = allocation.getRoom();
        room.setCurrentOccupancy(Math.max(0, room.getCurrentOccupancy() - 1));
        room.setAvailable(true);
        roomRepository.save(room);

        allocation.setStatus(AllocationStatus.VACATED);
        allocation.setCheckOutDate(LocalDate.now());

        return allocationRepository.save(allocation);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Allocation> findById(Long id) {
        return allocationRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Allocation> getStudentAllocations(Long studentId) {
        return allocationRepository.findByStudentId(studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Allocation> getAllocationsByStatus(AllocationStatus status) {
        return allocationRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Allocation> getAllAllocations() {
        return allocationRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasActiveAllocation(Long studentId) {
        return allocationRepository.hasActiveAllocation(studentId);
    }
}
