package com.hostel.service.impl;

import com.hostel.exception.InvalidOperationException;
import com.hostel.model.Complaint;
import com.hostel.model.User;
import com.hostel.model.enums.ComplaintStatus;
import com.hostel.model.enums.Role;
import com.hostel.pattern.observer.ComplaintEventPublisher;
import com.hostel.pattern.observer.ComplaintObserver;
import com.hostel.repository.ComplaintRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for ComplaintServiceImpl.
 * Tests complaint lifecycle transitions and Observer pattern integration.
 *
 * Note: ComplaintEventPublisher is constructed manually (not mocked) because
 * Mockito's inline mock maker cannot mock concrete classes on Java 24.
 */
@ExtendWith(MockitoExtension.class)
class ComplaintServiceImplTest {

    @Mock
    private ComplaintRepository complaintRepository;

    private ComplaintServiceImpl complaintService;

    private User student;
    private User warden;

    // A test observer to verify notifications
    private TestObserver testObserver;

    static class TestObserver implements ComplaintObserver {
        boolean notified = false;
        String lastOldStatus;
        String lastNewStatus;

        @Override
        public void onComplaintStatusChanged(Long complaintId, String studentName,
                                              String title, String oldStatus, String newStatus) {
            this.notified = true;
            this.lastOldStatus = oldStatus;
            this.lastNewStatus = newStatus;
        }

        void reset() {
            notified = false;
            lastOldStatus = null;
            lastNewStatus = null;
        }
    }

    @BeforeEach
    void setUp() {
        student = new User("student1", "pass", "Test Student", "student@test.com", "1234567890", Role.STUDENT);
        student.setId(1L);

        warden = new User("warden1", "pass", "Test Warden", "warden@test.com", "0987654321", Role.WARDEN);
        warden.setId(2L);

        testObserver = new TestObserver();
        ArrayList<ComplaintObserver> observers = new ArrayList<>();
        observers.add(testObserver);
        ComplaintEventPublisher eventPublisher = new ComplaintEventPublisher(observers);

        complaintService = new ComplaintServiceImpl(complaintRepository, eventPublisher);
    }

    @Test
    @DisplayName("Submit complaint — should save and notify observers")
    void submitComplaint_success() {
        when(complaintRepository.save(any(Complaint.class))).thenAnswer(invocation -> {
            Complaint c = invocation.getArgument(0);
            c.setId(1L);
            return c;
        });

        Complaint complaint = complaintService.submitComplaint(student, "Broken Fan",
                "The ceiling fan in room A101 is not working", "Maintenance");

        assertNotNull(complaint);
        assertEquals("Broken Fan", complaint.getTitle());
        assertEquals(ComplaintStatus.SUBMITTED, complaint.getStatus());
        assertTrue(testObserver.notified, "Observer should have been notified");
    }

    // ═══════════════ Status Transitions ═══════════════

    @Test
    @DisplayName("Status: SUBMITTED → IN_PROGRESS — valid transition")
    void updateStatus_submittedToInProgress_success() {
        Complaint complaint = new Complaint(student, "Bug", "Desc", "IT");
        complaint.setId(1L);
        complaint.setStatus(ComplaintStatus.SUBMITTED);

        when(complaintRepository.findById(1L)).thenReturn(Optional.of(complaint));
        when(complaintRepository.save(any(Complaint.class))).thenAnswer(i -> i.getArgument(0));

        Complaint result = complaintService.updateComplaintStatus(1L,
                ComplaintStatus.IN_PROGRESS, warden, "Working on it");

        assertEquals(ComplaintStatus.IN_PROGRESS, result.getStatus());
        assertTrue(testObserver.notified);
        assertEquals("SUBMITTED", testObserver.lastOldStatus);
        assertEquals("IN_PROGRESS", testObserver.lastNewStatus);
    }

    @Test
    @DisplayName("Status: IN_PROGRESS → RESOLVED — valid transition")
    void updateStatus_inProgressToResolved_success() {
        Complaint complaint = new Complaint(student, "Bug", "Desc", "IT");
        complaint.setId(1L);
        complaint.setStatus(ComplaintStatus.IN_PROGRESS);

        when(complaintRepository.findById(1L)).thenReturn(Optional.of(complaint));
        when(complaintRepository.save(any(Complaint.class))).thenAnswer(i -> i.getArgument(0));

        Complaint result = complaintService.updateComplaintStatus(1L,
                ComplaintStatus.RESOLVED, warden, "Fixed the issue");

        assertEquals(ComplaintStatus.RESOLVED, result.getStatus());
        assertNotNull(result.getResolvedAt());
    }

    @Test
    @DisplayName("Status: RESOLVED → CLOSED — valid transition")
    void updateStatus_resolvedToClosed_success() {
        Complaint complaint = new Complaint(student, "Bug", "Desc", "IT");
        complaint.setId(1L);
        complaint.setStatus(ComplaintStatus.RESOLVED);

        when(complaintRepository.findById(1L)).thenReturn(Optional.of(complaint));
        when(complaintRepository.save(any(Complaint.class))).thenAnswer(i -> i.getArgument(0));

        Complaint result = complaintService.updateComplaintStatus(1L,
                ComplaintStatus.CLOSED, warden, "Confirmed resolved");

        assertEquals(ComplaintStatus.CLOSED, result.getStatus());
    }

    @Test
    @DisplayName("Status: SUBMITTED → RESOLVED — INVALID transition should throw")
    void updateStatus_submittedToResolved_shouldThrow() {
        Complaint complaint = new Complaint(student, "Bug", "Desc", "IT");
        complaint.setId(1L);
        complaint.setStatus(ComplaintStatus.SUBMITTED);

        when(complaintRepository.findById(1L)).thenReturn(Optional.of(complaint));

        assertThrows(InvalidOperationException.class,
                () -> complaintService.updateComplaintStatus(1L,
                        ComplaintStatus.RESOLVED, warden, "notes"));
    }

    @Test
    @DisplayName("Status: CLOSED → any — INVALID transition should throw")
    void updateStatus_fromClosed_shouldThrow() {
        Complaint complaint = new Complaint(student, "Bug", "Desc", "IT");
        complaint.setId(1L);
        complaint.setStatus(ComplaintStatus.CLOSED);

        when(complaintRepository.findById(1L)).thenReturn(Optional.of(complaint));

        assertThrows(InvalidOperationException.class,
                () -> complaintService.updateComplaintStatus(1L,
                        ComplaintStatus.IN_PROGRESS, warden, "reopen"));
    }

    @Test
    @DisplayName("Status: IN_PROGRESS → SUBMITTED — INVALID backward transition should throw")
    void updateStatus_inProgressToSubmitted_shouldThrow() {
        Complaint complaint = new Complaint(student, "Bug", "Desc", "IT");
        complaint.setId(1L);
        complaint.setStatus(ComplaintStatus.IN_PROGRESS);

        when(complaintRepository.findById(1L)).thenReturn(Optional.of(complaint));

        assertThrows(InvalidOperationException.class,
                () -> complaintService.updateComplaintStatus(1L,
                        ComplaintStatus.SUBMITTED, warden, "notes"));
    }
}
