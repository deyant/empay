package com.example.empay.exception;

/**
 * Exception indicating that a financial transaction cannot be completed for
 * reasons such as business rules and constraints.
 */
public class TransactionValidationException extends RuntimeException {

    /**
     * Constructor with a message.
     *
     * @param message Exception message.
     */
    public TransactionValidationException(final String message) {
        super(message);
    }
}
