package com.hostel.pattern.strategy;

import com.hostel.model.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Concrete Strategy: Processes cash payments.
 */
@Component("cashPaymentStrategy")
public class CashPaymentStrategy implements PaymentStrategy {

    private static final Logger logger = LoggerFactory.getLogger(CashPaymentStrategy.class);

    @Override
    public String processPayment(Payment payment) {
        String transactionId = "CASH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        logger.info("💵 Processing CASH payment of ₹{} — Transaction ID: {}",
                payment.getAmount(), transactionId);
        // In production: record cash receipt, generate physical receipt
        return transactionId;
    }

    @Override
    public String getStrategyName() {
        return "Cash Payment";
    }
}
