package com.hostel.model.enums;

/**
 * Represents the lifecycle status of a complaint.
 * Follows the Observer pattern — status changes trigger notifications.
 */
public enum ComplaintStatus {
    SUBMITTED,     // Complaint submitted by student
    IN_PROGRESS,   // Warden/maintenance is working on it
    RESOLVED,      // Issue resolved
    CLOSED         // Complaint closed after resolution
}
