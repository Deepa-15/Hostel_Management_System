package com.hostel.pattern.strategy;

import com.hostel.model.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Concrete Strategy: Processes online payments.
 */
@Component("onlinePaymentStrategy")
public class OnlinePaymentStrategy implements PaymentStrategy {

    private static final Logger logger = LoggerFactory.getLogger(OnlinePaymentStrategy.class);

    @Override
    public String processPayment(Payment payment) {
        String transactionId = "ONL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        logger.info("💳 Processing ONLINE payment of ₹{} — Transaction ID: {}",
                payment.getAmount(), transactionId);
        // In production: integrate with payment gateway (Razorpay, Stripe, etc.)
        return transactionId;
    }

    @Override
    public String getStrategyName() {
        return "Online Payment";
    }
}
