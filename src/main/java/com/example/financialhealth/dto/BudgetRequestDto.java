package com.example.financialhealth.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class BudgetRequestDto {

    @NotBlank(message = "Category cannot be blank.")
    @Size(max = 100, message = "Category cannot exceed 100 characters.")
    private String category;

    @NotNull(message = "Allocated amount cannot be null.")
    @DecimalMin(value = "0.00", message = "Allocated amount must be zero or positive.")
    private BigDecimal allocatedAmount;

    @NotBlank(message = "Month cannot be blank.")
    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])$", message = "Month must be in YYYY-MM format.")
    private String month; // YYYY-MM format

    @DecimalMin(value = "0.00", message = "Total monthly budget goal must be zero or positive.")
    private BigDecimal totalMonthlyBudgetGoal; // Optional

    public BudgetRequestDto() {
    }

    public BudgetRequestDto(String category, BigDecimal allocatedAmount, String month, BigDecimal totalMonthlyBudgetGoal) {
        this.category = category;
        this.allocatedAmount = allocatedAmount;
        this.month = month;
        this.totalMonthlyBudgetGoal = totalMonthlyBudgetGoal;
    }

    // Getters and Setters
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
}
