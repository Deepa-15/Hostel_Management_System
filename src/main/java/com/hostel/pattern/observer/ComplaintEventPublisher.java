package com.hostel.pattern.observer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Event Publisher that maintains a list of observers and notifies them
 * when a complaint status changes (Subject in Observer pattern).
 * 
 * Spring automatically injects all beans implementing ComplaintObserver
 * via constructor injection — demonstrating DIP.
 */
@Component
public class ComplaintEventPublisher {

    private final List<ComplaintObserver> observers;

    @Autowired
    public ComplaintEventPublisher(List<ComplaintObserver> observers) {
        this.observers = observers;
    }

    /**
     * Notify all registered observers about a complaint status change.
     */
    public void notifyObservers(Long complaintId, String studentName,
                                 String title, String oldStatus, String newStatus) {
        for (ComplaintObserver observer : observers) {
            observer.onComplaintStatusChanged(complaintId, studentName, title, oldStatus, newStatus);
        }
    }
}
