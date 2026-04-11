package com.hostel.pattern.observer;

/**
 * DESIGN PATTERN - Observer Pattern (Behavioral):
 * 
 * WHY: The Observer Pattern is used to implement a notification system
 * for complaint status changes. When a complaint's status changes
 * (e.g., from SUBMITTED to IN_PROGRESS to RESOLVED), all registered
 * observers are notified automatically. This decouples the complaint
 * management logic from the notification logic.
 * 
 * WHERE: Implemented in the ComplaintService. When a complaint status
 * changes, the ComplaintEventPublisher notifies all registered
 * ComplaintObserver instances.
 * 
 * DESIGN PRINCIPLE - Dependency Inversion Principle (DIP):
 * The service depends on the ComplaintObserver abstraction (interface),
 * not on concrete notification implementations. This allows adding
 * new notification channels (email, SMS, push) without modifying
 * the complaint service.
 */
public interface ComplaintObserver {

    /**
     * Called when a complaint status changes.
     *
     * @param complaintId   ID of the complaint
     * @param studentName   Name of the student who filed the complaint
     * @param title         Complaint title
     * @param oldStatus     Previous status
     * @param newStatus     New status
     */
    void onComplaintStatusChanged(Long complaintId, String studentName,
                                   String title, String oldStatus, String newStatus);
}
