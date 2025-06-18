package com.example.financialhealth.service;

import com.example.financialhealth.dto.insights.InsightRequestDto;
import com.example.financialhealth.dto.insights.InsightResponseDto;
import com.example.financialhealth.dto.insights.WeeklySummaryResponseDto;
import java.time.LocalDate;

public interface FinancialInsightService {
    WeeklySummaryResponseDto getWeeklySummary(Long userId, LocalDate startDate, LocalDate endDate);
    InsightResponseDto generateLlmInsight(Long userId, String userQuery); // Changed from InsightRequestDto to String for simplicity here
}
