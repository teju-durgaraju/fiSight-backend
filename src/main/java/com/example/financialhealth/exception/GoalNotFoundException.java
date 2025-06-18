package com.example.financialhealth.exception;

// @ResponseStatus can be added here if a default behavior is desired without
// a specific handler in GlobalExceptionHandler.
// For this project, GlobalExceptionHandler will be updated to handle this explicitly
// to ensure a consistent JSON error response structure.
public class GoalNotFoundException extends RuntimeException {
    public GoalNotFoundException(String message) {
        super(message);
    }
}
