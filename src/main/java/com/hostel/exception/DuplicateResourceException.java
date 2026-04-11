package com.hostel.exception;

/**
 * Custom exception for duplicate or conflicting resource creation.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
