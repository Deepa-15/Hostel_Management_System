package com.hostel.model.enums;

/**
 * Represents the lifecycle status of a room allocation request.
 */
public enum AllocationStatus {
    PENDING,      // Student has applied, awaiting warden approval
    APPROVED,     // Warden has approved, awaiting fee payment
    CONFIRMED,    // Fee paid and allocation confirmed
    REJECTED,     // Warden rejected the application
    VACATED       // Student has vacated the room
}
