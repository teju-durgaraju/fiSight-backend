package com.example.financialhealth.exception;

// import org.springframework.http.HttpStatus;
// import org.springframework.web.bind.annotation.ResponseStatus;

// @ResponseStatus(HttpStatus.NOT_FOUND) // This is now handled by GlobalExceptionHandler for consistent response body
public class BudgetNotFoundException extends RuntimeException {
    public BudgetNotFoundException(String message) {
        super(message);
    }
}
