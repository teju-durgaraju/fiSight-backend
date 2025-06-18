package com.example.financialhealth.dto;

import com.example.financialhealth.model.enums.TransactionType; // Using enum directly for type safety
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

/**
 * Data Transfer Object for sending transaction details to the client.
 * Conversion logic between Transaction Entity and this DTO is typically handled
 * in a service layer or a dedicated mapper component.
 */
public class TransactionResponseDto {

    private Long id;
    private Long userId;
    private TransactionType type; // Using enum directly for better type safety on response
    private BigDecimal amount;
    private String categoryName; // Changed from category to categoryName
    private LocalDate transactionDate;
    private String description;
    private Timestamp createdAt;

    public TransactionResponseDto() {
    }

    public TransactionResponseDto(Long id, Long userId, TransactionType type, BigDecimal amount, String categoryName, LocalDate transactionDate, String description, Timestamp createdAt) {
        this.id = id;
        this.userId = userId;
        this.type = type;
        this.amount = amount;
        this.categoryName = categoryName; // Changed from category to categoryName
        this.transactionDate = transactionDate;
        this.description = description;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCategoryName() { // Changed from getCategory to getCategoryName
        return categoryName;
    }

    public void setCategoryName(String categoryName) { // Changed from setCategory to setCategoryName
        this.categoryName = categoryName;
    }

    public LocalDate getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
