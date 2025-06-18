package com.example.financialhealth.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;

public class BudgetResponseDto {

    private Long id;
    private Long userId;
    private String category;
    private BigDecimal allocatedAmount;
    private String month; // YYYY-MM format
    private BigDecimal totalMonthlyBudgetGoal;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public BudgetResponseDto() {
    }

    public BudgetResponseDto(Long id, Long userId, String category, BigDecimal allocatedAmount, String month, BigDecimal totalMonthlyBudgetGoal, Timestamp createdAt, Timestamp updatedAt) {
        this.id = id;
        this.userId = userId;
        this.category = category;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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
