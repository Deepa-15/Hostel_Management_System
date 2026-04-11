package com.hostel.pattern.strategy;

import com.hostel.model.enums.PaymentMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Context class for the Strategy Pattern.
 * Selects the appropriate PaymentStrategy based on the PaymentMethod.
 * 
 * Uses Spring's dependency injection to get all strategy beans,
 * demonstrating both Strategy Pattern and Spring's Singleton/DI patterns.
 */
@Component
public class PaymentStrategyContext {

    private final Map<String, PaymentStrategy> strategies;

    @Autowired
    public PaymentStrategyContext(Map<String, PaymentStrategy> strategies) {
        this.strategies = strategies;
    }

    /**
     * Get the payment strategy for the given payment method.
     */
    public PaymentStrategy getStrategy(PaymentMethod method) {
        return switch (method) {
            case CASH -> strategies.get("cashPaymentStrategy");
            case ONLINE -> strategies.get("onlinePaymentStrategy");
            case BANK_TRANSFER -> strategies.get("bankTransferPaymentStrategy");
        };
    }
}
