package com.hostel.service.impl;

import com.hostel.exception.InvalidOperationException;
import com.hostel.exception.ResourceNotFoundException;
import com.hostel.model.Allocation;
import com.hostel.model.Payment;
import com.hostel.model.User;
import com.hostel.model.enums.AllocationStatus;
import com.hostel.model.enums.PaymentMethod;
import com.hostel.model.enums.PaymentStatus;
import com.hostel.pattern.strategy.PaymentStrategy;
import com.hostel.pattern.strategy.PaymentStrategyContext;
import com.hostel.repository.PaymentRepository;
import com.hostel.service.AllocationService;
import com.hostel.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Payment service implementation.
 * 
 * DESIGN PATTERN - Strategy Pattern:
 * Uses PaymentStrategyContext to select the appropriate payment strategy
 * at runtime based on the chosen PaymentMethod.
 */
@Service
@Transactional
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final AllocationService allocationService;
    private final PaymentStrategyContext strategyContext;

    @Autowired
    public PaymentServiceImpl(PaymentRepository paymentRepository,
                               AllocationService allocationService,
                               PaymentStrategyContext strategyContext) {
        this.paymentRepository = paymentRepository;
        this.allocationService = allocationService;
        this.strategyContext = strategyContext;
    }

    @Override
    public Payment processPayment(User student, Long allocationId, PaymentMethod method) {
        Allocation allocation = allocationService.findById(allocationId)
                .orElseThrow(() -> new ResourceNotFoundException("Allocation", allocationId));

        // Verify allocation is approved (payment before confirmation)
        if (allocation.getStatus() != AllocationStatus.APPROVED) {
            throw new InvalidOperationException(
                "Payment can only be made for APPROVED allocations. Current status: " + allocation.getStatus());
        }

        // Verify the allocation belongs to this student
        if (!allocation.getStudent().getId().equals(student.getId())) {
            throw new InvalidOperationException("This allocation does not belong to you.");
        }

        double amount = allocation.getRoom().getFeePerSemester();
        Payment payment = new Payment(student, allocation, amount, method);

        // Use Strategy Pattern to process payment
        PaymentStrategy strategy = strategyContext.getStrategy(method);
        String transactionId = strategy.processPayment(payment);

        payment.setTransactionId(transactionId);
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setDescription("Hostel fee payment for Room " + allocation.getRoom().getRoomNumber()
                + " via " + strategy.getStrategyName());

        Payment savedPayment = paymentRepository.save(payment);

        // Confirm the allocation after successful payment
        allocationService.confirmAllocation(allocationId);

        return savedPayment;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Payment> findById(Long id) {
        return paymentRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByStudent(Long studentId) {
        return paymentRepository.findByStudentId(studentId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Payment> getPaymentsByAllocation(Long allocationId) {
        return paymentRepository.findByAllocationId(allocationId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }
}
