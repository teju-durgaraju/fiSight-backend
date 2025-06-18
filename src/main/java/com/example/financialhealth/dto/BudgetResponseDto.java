package com.example.financialhealth.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class BudgetResponseDto {

    private Long id;
    private Long userId;
    private String categoryName; // Changed from category to categoryName
    private BigDecimal allocatedAmount;
    private String month; // YYYY-MM format
    private BigDecimal totalMonthlyBudgetGoal;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public BudgetResponseDto() {
    }

    public BudgetResponseDto(Long id, Long userId, String categoryName, BigDecimal allocatedAmount, String month, BigDecimal totalMonthlyBudgetGoal, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.userId = userId;
        this.categoryName = categoryName; // Changed
        this.allocatedAmount = allocatedAmount;
        this.month = month;
        this.totalMonthlyBudgetGoal = totalMonthlyBudgetGoal;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public BigDecimal getAllocatedAmount() {
        return allocatedAmount;
    }

    public void setAllocatedAmount(BigDecimal allocatedAmount) {
        this.allocatedAmount = allocatedAmount;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public BigDecimal getTotalMonthlyBudgetGoal() {
        return totalMonthlyBudgetGoal;
    }

    public void setTotalMonthlyBudgetGoal(BigDecimal totalMonthlyBudgetGoal) {
        this.totalMonthlyBudgetGoal = totalMonthlyBudgetGoal;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Timestamp getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Timestamp updatedAt) {
        this.updatedAt = updatedAt;
    }
}
