package com.hostel.repository;

import com.hostel.model.Allocation;
import com.hostel.model.enums.AllocationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AllocationRepository extends JpaRepository<Allocation, Long> {

    List<Allocation> findByStudentId(Long studentId);

    List<Allocation> findByStatus(AllocationStatus status);

    List<Allocation> findByRoomId(Long roomId);

    @Query("SELECT a FROM Allocation a WHERE a.student.id = :studentId AND a.status IN ('PENDING', 'APPROVED', 'CONFIRMED')")
    List<Allocation> findActiveAllocationsByStudent(@Param("studentId") Long studentId);

    @Query("SELECT a FROM Allocation a WHERE a.room.id = :roomId AND a.status IN ('CONFIRMED')")
    List<Allocation> findConfirmedAllocationsByRoom(@Param("roomId") Long roomId);

    /**
     * Check if a student already has a pending or active allocation — prevents double booking.
     */
    @Query("SELECT COUNT(a) > 0 FROM Allocation a WHERE a.student.id = :studentId AND a.status IN ('PENDING', 'APPROVED', 'CONFIRMED')")
    boolean hasActiveAllocation(@Param("studentId") Long studentId);
}
