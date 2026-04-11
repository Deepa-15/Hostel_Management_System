package com.hostel.service.impl;

import com.hostel.exception.InvalidOperationException;
import com.hostel.exception.ResourceNotFoundException;
import com.hostel.model.Complaint;
import com.hostel.model.User;
import com.hostel.model.enums.ComplaintStatus;
import com.hostel.pattern.observer.ComplaintEventPublisher;
import com.hostel.repository.ComplaintRepository;
import com.hostel.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Complaint service implementation.
 * 
 * DESIGN PATTERN - Observer Pattern:
 * Uses ComplaintEventPublisher to notify observers when a complaint
 * status changes. Currently, EmailNotificationObserver and
 * DashboardNotificationObserver are registered as observers.
 */
@Service
@Transactional
public class ComplaintServiceImpl implements ComplaintService {

    private final ComplaintRepository complaintRepository;
    private final ComplaintEventPublisher eventPublisher;

    @Autowired
    public ComplaintServiceImpl(ComplaintRepository complaintRepository,
                                 ComplaintEventPublisher eventPublisher) {
        this.complaintRepository = complaintRepository;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Complaint submitComplaint(User student, String title, String description, String category) {
        Complaint complaint = new Complaint(student, title, description, category);
        Complaint saved = complaintRepository.save(complaint);

        // Notify observers about new complaint
        eventPublisher.notifyObservers(saved.getId(), student.getFullName(),
                title, "NEW", ComplaintStatus.SUBMITTED.name());

        return saved;
    }

    @Override
    public Complaint updateComplaintStatus(Long complaintId, ComplaintStatus newStatus,
                                            User resolvedBy, String notes) {
        Complaint complaint = complaintRepository.findById(complaintId)
                .orElseThrow(() -> new ResourceNotFoundException("Complaint", complaintId));

        String oldStatus = complaint.getStatus().name();

        // Validate status transitions
        validateStatusTransition(complaint.getStatus(), newStatus);

        complaint.setStatus(newStatus);
        if (notes != null && !notes.isBlank()) {
            complaint.setResolutionNotes(notes);
        }
        if (resolvedBy != null) {
            complaint.setResolvedBy(resolvedBy);
        }
        if (newStatus == ComplaintStatus.RESOLVED || newStatus == ComplaintStatus.CLOSED) {
            complaint.setResolvedAt(LocalDateTime.now());
        }

        Complaint updated = complaintRepository.save(complaint);

        // Observer Pattern: Notify all observers about the status change
        eventPublisher.notifyObservers(complaint.getId(),
                complaint.getStudent().getFullName(),
                complaint.getTitle(), oldStatus, newStatus.name());

        return updated;
    }

    /**
     * Validates allowed complaint status transitions.
     */
    private void validateStatusTransition(ComplaintStatus current, ComplaintStatus next) {
        boolean valid = switch (current) {
            case SUBMITTED -> next == ComplaintStatus.IN_PROGRESS || next == ComplaintStatus.CLOSED;
            case IN_PROGRESS -> next == ComplaintStatus.RESOLVED || next == ComplaintStatus.CLOSED;
            case RESOLVED -> next == ComplaintStatus.CLOSED;
            case CLOSED -> false; // Cannot transition from CLOSED
        };

        if (!valid) {
            throw new InvalidOperationException(
                "Invalid status transition: " + current + " → " + next);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Complaint> findById(Long id) {
        return complaintRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Complaint> getComplaintsByStudent(Long studentId) {
        return complaintRepository.findByStudentId(studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Complaint> getComplaintsByStatus(ComplaintStatus status) {
        return complaintRepository.findByStatus(status);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Complaint> getAllComplaints() {
        return complaintRepository.findAll();
    }
}
