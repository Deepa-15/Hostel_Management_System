package com.hostel.exception;

/**
 * Custom exception for invalid business operations.
 */
public class InvalidOperationException extends RuntimeException {

    public InvalidOperationException(String message) {
        super(message);
    }
}
