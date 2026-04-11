package com.hostel.repository;

import com.hostel.model.Complaint;
import com.hostel.model.enums.ComplaintStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplaintRepository extends JpaRepository<Complaint, Long> {
    List<Complaint> findByStudentId(Long studentId);
    List<Complaint> findByStatus(ComplaintStatus status);
    List<Complaint> findByStudentIdAndStatus(Long studentId, ComplaintStatus status);
}
