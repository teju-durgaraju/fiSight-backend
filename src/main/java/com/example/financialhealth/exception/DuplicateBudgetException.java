package com.example.financialhealth.exception;

// This exception can be handled by GlobalExceptionHandler to return a 400 or 409 (Conflict)
// For now, a general 400 via IllegalArgumentException handler or a specific handler can be added later.
// If a specific HTTP status is desired without an explicit handler, use @ResponseStatus.
// For example: @org.springframework.web.bind.annotation.ResponseStatus(org.springframework.http.HttpStatus.CONFLICT)
public class DuplicateBudgetException extends RuntimeException {
    public DuplicateBudgetException(String message) {
        super(message);
    }
}
