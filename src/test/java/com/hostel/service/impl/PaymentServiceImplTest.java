package com.hostel.service.impl;

import com.hostel.exception.InvalidOperationException;
import com.hostel.model.Allocation;
import com.hostel.model.Payment;
import com.hostel.model.Room;
import com.hostel.model.User;
import com.hostel.model.enums.*;
import com.hostel.pattern.strategy.CashPaymentStrategy;
import com.hostel.pattern.strategy.PaymentStrategy;
import com.hostel.pattern.strategy.PaymentStrategyContext;
import com.hostel.repository.PaymentRepository;
import com.hostel.service.AllocationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for PaymentServiceImpl.
 * Tests payment processing, strategy selection, and business rule enforcement.
 *
 * Note: PaymentStrategyContext is constructed manually (not mocked) because
 * Mockito's inline mock maker cannot mock concrete classes on Java 24.
 * AllocationService is an interface and can be mocked normally.
 */
@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private AllocationService allocationService;

    private PaymentServiceImpl paymentService;

    private User student;
    private Room room;
    private Allocation allocation;

    @BeforeEach
    void setUp() {
        student = new User("student1", "pass", "Test Student", "student@test.com", "1234567890", Role.STUDENT);
        student.setId(1L);

        room = new Room("A101", RoomType.SINGLE, 1, 5000.0, 1, null);
        room.setId(1L);

        allocation = new Allocation(student, room);
        allocation.setId(1L);
        allocation.setStatus(AllocationStatus.APPROVED);

        // Construct real PaymentStrategyContext with actual strategies
        PaymentStrategy cashStrategy = new CashPaymentStrategy();
        Map<String, PaymentStrategy> strategies = Map.of(
                "cashPaymentStrategy", cashStrategy
        );
        PaymentStrategyContext strategyContext = new PaymentStrategyContext(strategies);

        paymentService = new PaymentServiceImpl(paymentRepository, allocationService, strategyContext);
    }

    @Test
    @DisplayName("Process payment — success with CASH method")
    void processPayment_success() {
        when(allocationService.findById(1L)).thenReturn(Optional.of(allocation));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));
        when(allocationService.confirmAllocation(1L)).thenReturn(allocation);

        Payment payment = paymentService.processPayment(student, 1L, PaymentMethod.CASH);

        assertNotNull(payment);
        assertEquals(5000.0, payment.getAmount());
        assertEquals(PaymentStatus.COMPLETED, payment.getStatus());
        assertNotNull(payment.getTransactionId());
        assertTrue(payment.getTransactionId().startsWith("CASH-"));
        verify(allocationService).confirmAllocation(1L);
    }

    @Test
    @DisplayName("Process payment — should throw when allocation is not APPROVED")
    void processPayment_notApproved_shouldThrow() {
        allocation.setStatus(AllocationStatus.PENDING);
        when(allocationService.findById(1L)).thenReturn(Optional.of(allocation));

        assertThrows(InvalidOperationException.class,
                () -> paymentService.processPayment(student, 1L, PaymentMethod.CASH));
    }

    @Test
    @DisplayName("Process payment — should throw when allocation belongs to a different student")
    void processPayment_wrongStudent_shouldThrow() {
        User otherStudent = new User("other", "pass", "Other", "other@test.com", "111", Role.STUDENT);
        otherStudent.setId(99L);

        when(allocationService.findById(1L)).thenReturn(Optional.of(allocation));

        assertThrows(InvalidOperationException.class,
                () -> paymentService.processPayment(otherStudent, 1L, PaymentMethod.CASH));
    }

    @Test
    @DisplayName("Process payment — should confirm allocation after successful payment")
    void processPayment_shouldConfirmAllocationAfterPayment() {
        when(allocationService.findById(1L)).thenReturn(Optional.of(allocation));
        when(paymentRepository.save(any(Payment.class))).thenAnswer(i -> i.getArgument(0));
        when(allocationService.confirmAllocation(1L)).thenReturn(allocation);

        paymentService.processPayment(student, 1L, PaymentMethod.CASH);

        verify(allocationService, times(1)).confirmAllocation(1L);
    }
}
