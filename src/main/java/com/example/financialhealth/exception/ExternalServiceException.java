package com.example.financialhealth.exception;

// This exception can be handled by GlobalExceptionHandler to return a 500 or 502/503 status.
// For now, a general 500 via the generic Exception handler is okay.
// If a specific HTTP status is desired without an explicit handler, use @ResponseStatus.
public class ExternalServiceException extends RuntimeException {
    public ExternalServiceException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExternalServiceException(String message) {
        super(message);
    }
}
