package com.hostel.pattern.strategy;

import com.hostel.model.Payment;
import com.hostel.model.enums.PaymentMethod;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Payment Strategy Pattern.
 * Tests each concrete strategy implementation independently.
 */
class PaymentStrategyTest {

    @Test
    @DisplayName("CashPaymentStrategy should generate CASH-prefixed transaction ID")
    void cashStrategy_shouldGenerateCashTransactionId() {
        PaymentStrategy strategy = new CashPaymentStrategy();
        Payment payment = new Payment();
        payment.setAmount(5000.0);

        String transactionId = strategy.processPayment(payment);

        assertNotNull(transactionId);
        assertTrue(transactionId.startsWith("CASH-"), "Transaction ID should start with CASH-");
        assertEquals("Cash Payment", strategy.getStrategyName());
    }

    @Test
    @DisplayName("OnlinePaymentStrategy should generate ONLINE-prefixed transaction ID")
    void onlineStrategy_shouldGenerateOnlineTransactionId() {
        PaymentStrategy strategy = new OnlinePaymentStrategy();
        Payment payment = new Payment();
        payment.setAmount(7500.0);

        String transactionId = strategy.processPayment(payment);

        assertNotNull(transactionId);
        assertTrue(transactionId.startsWith("ONL-"), "Transaction ID should start with ONL-");
        assertEquals("Online Payment", strategy.getStrategyName());
    }

    @Test
    @DisplayName("BankTransferPaymentStrategy should generate BANK-prefixed transaction ID")
    void bankTransferStrategy_shouldGenerateBankTransactionId() {
        PaymentStrategy strategy = new BankTransferPaymentStrategy();
        Payment payment = new Payment();
        payment.setAmount(10000.0);

        String transactionId = strategy.processPayment(payment);

        assertNotNull(transactionId);
        assertTrue(transactionId.startsWith("BANK-"), "Transaction ID should start with BANK-");
        assertEquals("Bank Transfer", strategy.getStrategyName());
    }

    @Test
    @DisplayName("Each strategy call should produce unique transaction IDs")
    void strategies_shouldGenerateUniqueTransactionIds() {
        PaymentStrategy cashStrategy = new CashPaymentStrategy();
        Payment payment = new Payment();
        payment.setAmount(1000.0);

        String txn1 = cashStrategy.processPayment(payment);
        String txn2 = cashStrategy.processPayment(payment);

        assertNotEquals(txn1, txn2, "Each payment should have a unique transaction ID");
    }
}
