package com.hostel.pattern.observer;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Observer Pattern (ComplaintEventPublisher + ComplaintObserver).
 * Verifies that all registered observers are notified on status changes.
 */
class ComplaintObserverTest {

    /**
     * Stub observer that records invocations for test verification.
     */
    static class TestObserver implements ComplaintObserver {
        boolean notified = false;
        String lastOldStatus;
        String lastNewStatus;
        Long lastComplaintId;

        @Override
        public void onComplaintStatusChanged(Long complaintId, String studentName,
                                              String title, String oldStatus, String newStatus) {
            this.notified = true;
            this.lastComplaintId = complaintId;
            this.lastOldStatus = oldStatus;
            this.lastNewStatus = newStatus;
        }
    }

    @Test
    @DisplayName("Publisher should notify all registered observers")
    void notifyObservers_shouldCallAllObservers() {
        TestObserver observer1 = new TestObserver();
        TestObserver observer2 = new TestObserver();

        List<ComplaintObserver> observers = new ArrayList<>();
        observers.add(observer1);
        observers.add(observer2);

        ComplaintEventPublisher publisher = new ComplaintEventPublisher(observers);
        publisher.notifyObservers(1L, "John", "Broken Fan", "SUBMITTED", "IN_PROGRESS");

        assertTrue(observer1.notified, "Observer 1 should be notified");
        assertTrue(observer2.notified, "Observer 2 should be notified");
        assertEquals(1L, observer1.lastComplaintId);
        assertEquals("SUBMITTED", observer1.lastOldStatus);
        assertEquals("IN_PROGRESS", observer1.lastNewStatus);
    }

    @Test
    @DisplayName("Publisher should handle empty observer list gracefully")
    void notifyObservers_withNoObservers_shouldNotThrow() {
        ComplaintEventPublisher publisher = new ComplaintEventPublisher(new ArrayList<>());
        assertDoesNotThrow(() ->
            publisher.notifyObservers(1L, "Jane", "Water leak", "NEW", "SUBMITTED")
        );
    }

    @Test
    @DisplayName("Publisher should pass correct data to observers")
    void notifyObservers_shouldPassCorrectData() {
        TestObserver observer = new TestObserver();
        List<ComplaintObserver> observers = List.of(observer);

        ComplaintEventPublisher publisher = new ComplaintEventPublisher(observers);
        publisher.notifyObservers(42L, "Alice", "No WiFi", "IN_PROGRESS", "RESOLVED");

        assertEquals(42L, observer.lastComplaintId);
        assertEquals("IN_PROGRESS", observer.lastOldStatus);
        assertEquals("RESOLVED", observer.lastNewStatus);
    }
}
