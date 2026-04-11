package com.hostel.pattern.facade;

import com.hostel.model.*;
import com.hostel.model.enums.AllocationStatus;
import com.hostel.model.enums.PaymentMethod;
import com.hostel.model.enums.PaymentStatus;
import com.hostel.service.AllocationService;
import com.hostel.service.ComplaintService;
import com.hostel.service.PaymentService;
import com.hostel.service.RoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * DESIGN PATTERN - Facade Pattern (Structural):
 * 
 * WHY: The Hostel Management System has multiple subsystems (Room, Allocation,
 * Payment, Complaint). The Facade Pattern provides a simplified, unified
 * interface to these complex subsystems. Instead of a controller interacting
 * with 4-5 different services, it can use the HostelManagementFacade for
 * common multi-step operations.
 * 
 * WHERE: Used in controllers like StudentController and WardenController
 * for operations that span multiple services (e.g., "apply for room"
 * requires RoomService + AllocationService + PaymentService).
 * 
 * DESIGN PRINCIPLE - Single Responsibility Principle (SRP):
 * Each service handles one domain area. The Facade coordinates them
 * without adding business logic of its own.
 */
@Component
public class HostelManagementFacade {

    private final RoomService roomService;
    private final AllocationService allocationService;
    private final PaymentService paymentService;
    private final ComplaintService complaintService;

    @Autowired
    public HostelManagementFacade(RoomService roomService,
                                   AllocationService allocationService,
                                   PaymentService paymentService,
                                   ComplaintService complaintService) {
        this.roomService = roomService;
        this.allocationService = allocationService;
        this.paymentService = paymentService;
        this.complaintService = complaintService;
    }

    // ═══════════════════════════════════════════════════
    // STUDENT OPERATIONS (Simplified interface)
    // ═══════════════════════════════════════════════════

    /**
     * Simplified: Apply for a room.
     * Checks room availability, prevents double booking, creates allocation.
     */
    public Allocation applyForRoom(User student, Long roomId) {
        return allocationService.applyForRoom(student, roomId);
    }

    /**
     * Simplified: Make a fee payment for an allocation.
     */
    public Payment makePayment(User student, Long allocationId, PaymentMethod method) {
        return paymentService.processPayment(student, allocationId, method);
    }

    /**
     * Simplified: Submit a complaint.
     */
    public Complaint submitComplaint(User student, String title, String description, String category) {
        return complaintService.submitComplaint(student, title, description, category);
    }

    /**
     * Simplified: View available rooms.
     */
    public List<Room> getAvailableRooms() {
        return roomService.getAvailableRooms();
    }

    /**
     * Simplified: Get student's allocation history.
     */
    public List<Allocation> getStudentAllocations(Long studentId) {
        return allocationService.getStudentAllocations(studentId);
    }

    /**
     * Simplified: Get student's payment history.
     */
    public List<Payment> getStudentPayments(Long studentId) {
        return paymentService.getPaymentsByStudent(studentId);
    }

    /**
     * Simplified: Get student's complaints.
     */
    public List<Complaint> getStudentComplaints(Long studentId) {
        return complaintService.getComplaintsByStudent(studentId);
    }

    // ═══════════════════════════════════════════════════
    // WARDEN OPERATIONS
    // ═══════════════════════════════════════════════════

    /**
     * Simplified: Approve a room allocation.
     */
    public Allocation approveAllocation(Long allocationId, User warden) {
        return allocationService.approveAllocation(allocationId, warden);
    }

    /**
     * Simplified: Reject a room allocation.
     */
    public Allocation rejectAllocation(Long allocationId, User warden, String remarks) {
        return allocationService.rejectAllocation(allocationId, warden, remarks);
    }

    /**
     * Simplified: Get all pending allocations.
     */
    public List<Allocation> getPendingAllocations() {
        return allocationService.getAllocationsByStatus(AllocationStatus.PENDING);
    }

    /**
     * Simplified: Vacate a student from a room.
     */
    public Allocation vacateRoom(Long allocationId) {
        return allocationService.vacateRoom(allocationId);
    }
}
