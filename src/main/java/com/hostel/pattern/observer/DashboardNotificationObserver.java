package com.hostel.pattern.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Concrete Observer: Logs complaint status changes to system dashboard.
 * Demonstrates the extensibility of the Observer pattern —
 * new observers can be added without modifying the publisher.
 */
@Component
public class DashboardNotificationObserver implements ComplaintObserver {

    private static final Logger logger = LoggerFactory.getLogger(DashboardNotificationObserver.class);

    @Override
    public void onComplaintStatusChanged(Long complaintId, String studentName,
                                          String title, String oldStatus, String newStatus) {
        logger.info("📊 DASHBOARD UPDATE: Complaint #{} '{}' — status now: {}",
                complaintId, title, newStatus);
        // In production: push to WebSocket for real-time dashboard updates
    }
}
