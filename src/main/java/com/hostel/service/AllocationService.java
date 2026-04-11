package com.hostel.service;

import com.hostel.model.Allocation;
import com.hostel.model.User;
import com.hostel.model.enums.AllocationStatus;

import java.util.List;
import java.util.Optional;

public interface AllocationService {
    Allocation applyForRoom(User student, Long roomId);
    Allocation approveAllocation(Long allocationId, User warden);
    Allocation rejectAllocation(Long allocationId, User warden, String remarks);
    Allocation confirmAllocation(Long allocationId);
    Allocation vacateRoom(Long allocationId);
    Optional<Allocation> findById(Long id);
    List<Allocation> getStudentAllocations(Long studentId);
    List<Allocation> getAllocationsByStatus(AllocationStatus status);
    List<Allocation> getAllAllocations();
    boolean hasActiveAllocation(Long studentId);
}
