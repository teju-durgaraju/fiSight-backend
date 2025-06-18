package com.example.financialhealth.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema; // Added

    @NotNull(message = "Category ID cannot be null.")
    private Long categoryId;

    @NotNull(message = "Allocated amount cannot be null.")
    @DecimalMin(value = "0.00", message = "Allocated amount must be zero or positive.")
    private BigDecimal allocatedAmount;

    @NotBlank(message = "Month cannot be blank.")
    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])$", message = "Month must be in YYYY-MM format.")
    @Schema(description = "Month for the budget in YYYY-MM format.", example = "2024-07") // Added
    private String month; // YYYY-MM format

    @DecimalMin(value = "0.00", message = "Total monthly budget goal must be zero or positive.")
    private BigDecimal totalMonthlyBudgetGoal; // Optional

    public BudgetRequestDto() {
    }

    public BudgetRequestDto(Long categoryId, BigDecimal allocatedAmount, String month, BigDecimal totalMonthlyBudgetGoal) {
        this.categoryId = categoryId;
        this.allocatedAmount = allocatedAmount;
        this.month = month;
        this.totalMonthlyBudgetGoal = totalMonthlyBudgetGoal;
    }

    // Getters and Setters
    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
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
}
