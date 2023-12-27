package com.example.empay.exception;

/**
 * Thrown during entity search operation to signal for an unsupported
 * search operation or entity configuraiton.
 */
public class SearchException extends RuntimeException {
    /**
     * Constructor with a message.
     *
     * @param message Exception message containing details about the error.
     */
    public SearchException(final String message) {
        super(message);
    }
}
