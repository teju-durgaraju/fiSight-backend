package com.example.financialhealth.exception;

// This will be handled by GlobalExceptionHandler to return a 404 status
public class CategoryNotFoundException extends RuntimeException {
    public CategoryNotFoundException(String message) {
        super(message);
    }
}
