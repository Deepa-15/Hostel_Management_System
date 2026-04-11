package com.hostel.pattern.strategy;

import com.hostel.model.Payment;

/**
 * DESIGN PATTERN - Strategy Pattern (Behavioral):
 * 
 * WHY: Different payment methods (Cash, Online, Bank Transfer) require
 * different processing logic. The Strategy Pattern allows us to define
 * a family of payment algorithms and make them interchangeable.
 * The client (PaymentService) selects the appropriate strategy at
 * runtime based on the chosen payment method.
 * 
 * WHERE: Used in PaymentService. When a student makes a payment,
 * the appropriate PaymentStrategy implementation is selected
 * based on the PaymentMethod.
 * 
 * DESIGN PRINCIPLE - Open/Closed Principle (OCP):
 * New payment methods can be added by creating new strategy
 * implementations without modifying existing code.
 * 
 * DESIGN PRINCIPLE - Dependency Inversion Principle (DIP):
 * PaymentService depends on the PaymentStrategy abstraction,
 * not on concrete payment processing implementations.
 */
public interface PaymentStrategy {

    /**
     * Process a payment using the specific strategy.
     *
     * @param payment The payment to process
     * @return Transaction ID
     */
    String processPayment(Payment payment);

    /**
     * Get the name of this payment strategy.
     */
    String getStrategyName();
}
