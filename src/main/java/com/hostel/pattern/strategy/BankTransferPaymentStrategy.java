package com.hostel.pattern.strategy;

import com.hostel.model.Payment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Concrete Strategy: Processes bank transfer payments.
 */
@Component("bankTransferPaymentStrategy")
public class BankTransferPaymentStrategy implements PaymentStrategy {

    private static final Logger logger = LoggerFactory.getLogger(BankTransferPaymentStrategy.class);

    @Override
    public String processPayment(Payment payment) {
        String transactionId = "BANK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        logger.info("🏦 Processing BANK TRANSFER of ₹{} — Transaction ID: {}",
                payment.getAmount(), transactionId);
        // In production: verify NEFT/RTGS/IMPS transaction
        return transactionId;
    }

    @Override
    public String getStrategyName() {
        return "Bank Transfer";
    }
}
