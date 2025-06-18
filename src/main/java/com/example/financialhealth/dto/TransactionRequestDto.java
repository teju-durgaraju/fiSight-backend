package com.example.financialhealth.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

// For validation annotations (e.g., @NotNull, @NotBlank, @Size, @Positive, @PastOrPresent) to take effect,
// ensure 'spring-boot-starter-validation' is included in your pom.xml:
// <dependency>
//     <groupId>org.springframework.boot</groupId>
//     <artifactId>spring-boot-starter-validation</artifactId>
// </dependency>
// (This dependency has now been added to pom.xml)

public class TransactionRequestDto {

    @NotBlank(message = "Transaction type cannot be blank. Must be INCOME or EXPENSE.")
    private String type; // Will be mapped to TransactionType enum in service

    @NotNull(message = "Amount cannot be null.")
    @DecimalMin(value = "0.01", message = "Amount must be positive and greater than 0.")
    private BigDecimal amount;

    @NotNull(message = "Category ID cannot be null.")
    private Long categoryId;

    @NotNull(message = "Transaction date cannot be null.")
    @PastOrPresent(message = "Transaction date cannot be in the future.")
    private LocalDate transactionDate;

    @Size(max = 255, message = "Description cannot exceed 255 characters.")
    private String description;

    public TransactionRequestDto() {
    }

    public TransactionRequestDto(String type, BigDecimal amount, Long categoryId, LocalDate transactionDate, String description) {
        this.type = type;
        this.amount = amount;
        this.categoryId = categoryId;
        this.transactionDate = transactionDate;
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getCategory() {
        return category;
    }

    // Removed getCategory() and setCategory(String)

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
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
}
