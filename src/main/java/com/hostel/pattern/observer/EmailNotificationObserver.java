package com.hostel.pattern.observer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * Concrete Observer: Logs complaint status changes.
 * In a production system, this would send email notifications.
 */
@Component
public class EmailNotificationObserver implements ComplaintObserver {

    private static final Logger logger = LoggerFactory.getLogger(EmailNotificationObserver.class);

    @Override
    public void onComplaintStatusChanged(Long complaintId, String studentName,
                                          String title, String oldStatus, String newStatus) {
        logger.info("📧 EMAIL NOTIFICATION: Complaint #{} '{}' by {} status changed: {} → {}",
                complaintId, title, studentName, oldStatus, newStatus);
        // In production: send actual email using JavaMailSender
    }
}
