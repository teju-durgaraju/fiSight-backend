package com.example.financialhealth.dto.insights;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class WeeklySummaryResponseDto {
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal totalIncome;
    private BigDecimal totalExpenses;
    private BigDecimal netSavings;
    private List<CategorySpendingDto> spendingByCategory;

    public WeeklySummaryResponseDto() {
    }

    public WeeklySummaryResponseDto(LocalDate startDate, LocalDate endDate, BigDecimal totalIncome, BigDecimal totalExpenses, BigDecimal netSavings, List<CategorySpendingDto> spendingByCategory) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalIncome = totalIncome;
        this.totalExpenses = totalExpenses;
        this.netSavings = netSavings;
        this.spendingByCategory = spendingByCategory;
    }

    // Getters and Setters
    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public BigDecimal getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(BigDecimal totalIncome) {
        this.totalIncome = totalIncome;
    }

    public BigDecimal getTotalExpenses() {
        return totalExpenses;
    }

    public void setTotalExpenses(BigDecimal totalExpenses) {
        this.totalExpenses = totalExpenses;
    }

    public BigDecimal getNetSavings() {
        return netSavings;
    }

    public void setNetSavings(BigDecimal netSavings) {
        this.netSavings = netSavings;
    }

    public List<CategorySpendingDto> getSpendingByCategory() {
        return spendingByCategory;
    }

    public void setSpendingByCategory(List<CategorySpendingDto> spendingByCategory) {
        this.spendingByCategory = spendingByCategory;
    }
}
