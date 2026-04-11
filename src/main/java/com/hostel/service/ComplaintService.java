package com.hostel.service;

import com.hostel.model.Complaint;
import com.hostel.model.User;
import com.hostel.model.enums.ComplaintStatus;

import java.util.List;
import java.util.Optional;

public interface ComplaintService {
    Complaint submitComplaint(User student, String title, String description, String category);
    Complaint updateComplaintStatus(Long complaintId, ComplaintStatus newStatus, User resolvedBy, String notes);
    Optional<Complaint> findById(Long id);
    List<Complaint> getComplaintsByStudent(Long studentId);
    List<Complaint> getComplaintsByStatus(ComplaintStatus status);
    List<Complaint> getAllComplaints();
}
