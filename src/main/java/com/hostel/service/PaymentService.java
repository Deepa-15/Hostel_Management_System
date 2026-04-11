package com.hostel.service;

import com.hostel.model.Payment;
import com.hostel.model.User;
import com.hostel.model.enums.PaymentMethod;

import java.util.List;
import java.util.Optional;

public interface PaymentService {
    Payment processPayment(User student, Long allocationId, PaymentMethod method);
    Optional<Payment> findById(Long id);
    List<Payment> getPaymentsByStudent(Long studentId);
    List<Payment> getPaymentsByAllocation(Long allocationId);
    List<Payment> getAllPayments();
}
