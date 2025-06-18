package com.example.financialhealth.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public class GoalRequestDto {

    @NotBlank(message = "Goal name cannot be blank.")
    @Size(max = 255, message = "Goal name cannot exceed 255 characters.")
    private String goalName;

    @NotNull(message = "Target amount cannot be null.")
    @DecimalMin(value = "0.01", message = "Target amount must be positive and greater than 0.")
    private BigDecimal targetAmount;

    @DecimalMin(value = "0.00", message = "Current amount must be zero or positive.")
    private BigDecimal currentAmount; // Optional in request, service will default to 0 if null for new goals

    @FutureOrPresent(message = "Target date must be in the present or future.")
    private LocalDate targetDate; // Optional

    public GoalRequestDto() {
    }

    public GoalRequestDto(String goalName, BigDecimal targetAmount, BigDecimal currentAmount, LocalDate targetDate) {
        this.goalName = goalName;
        this.targetAmount = targetAmount;
        this.currentAmount = currentAmount;
        this.targetDate = targetDate;
    }

    // Getters and Setters
    public String getGoalName() {
        return goalName;
    }

    public void setGoalName(String goalName) {
        this.goalName = goalName;
    }

    public BigDecimal getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(BigDecimal targetAmount) {
        this.targetAmount = targetAmount;
    }

    public BigDecimal getCurrentAmount() {
        return currentAmount;
    }

    public void setCurrentAmount(BigDecimal currentAmount) {
        this.currentAmount = currentAmount;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }
}
