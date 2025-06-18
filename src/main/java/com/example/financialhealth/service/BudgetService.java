package com.example.financialhealth.service;

import com.example.financialhealth.dto.BudgetRequestDto;
import com.example.financialhealth.dto.BudgetResponseDto;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface BudgetService {
    BudgetResponseDto createBudget(Long userId, BudgetRequestDto requestDto);
    List<BudgetResponseDto> getBudgets(Long userId, Optional<String> month); // month is YYYY-MM
    Optional<BudgetResponseDto> getBudgetByIdForUser(Long userId, Long budgetId);
    BudgetResponseDto updateBudget(Long userId, Long budgetId, BudgetRequestDto requestDto);
    void deleteBudget(Long userId, Long budgetId);
    BigDecimal calculateCurrentSpending(Long userId, String category, String month); // month is YYYY-MM
}
