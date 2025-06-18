package com.example.financialhealth.controller;

import com.example.financialhealth.dto.insights.InsightRequestDto;
import com.example.financialhealth.dto.insights.InsightResponseDto;
import com.example.financialhealth.dto.insights.WeeklySummaryResponseDto;
import com.example.financialhealth.model.User;
import com.example.financialhealth.service.FinancialInsightService;
import io.swagger.v3.oas.annotations.Operation; // Added
import io.swagger.v3.oas.annotations.tags.Tag; // Added
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Optional;

@Tag(name = "Financial Insights", description = "Endpoints for financial summaries and AI-powered advice.") // Added
@RestController
@RequestMapping("/api/v1/insights")
public class FinancialInsightController {

    private static final Logger logger = LoggerFactory.getLogger(FinancialInsightController.class);
    private final FinancialInsightService financialInsightService;

    public FinancialInsightController(FinancialInsightService financialInsightService) {
        this.financialInsightService = financialInsightService;
    }

    private User getAuthenticatedUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails instanceof User) {
            return (User) userDetails;
        }
        logger.error("User details are not of expected type (User). Actual type: {}", userDetails != null ? userDetails.getClass().getName() : "null");
        throw new InsufficientAuthenticationException("User details not found or not of expected type. Ensure you are authenticated correctly.");
    }

    @Operation(summary = "Get weekly financial summary", description = "Retrieves a summary of income, expenses, and net savings for a specified week (defaults to current week).") // Added
    @GetMapping("/weekly")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<WeeklySummaryResponseDto> getWeeklyFinancialSummary(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> startDateOpt,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> endDateOpt) {

        User authUser = getAuthenticatedUser(userDetails);
        LocalDate today = LocalDate.now();

        LocalDate startDate;
        LocalDate endDate;

        if (startDateOpt.isPresent() && endDateOpt.isPresent()) {
            startDate = startDateOpt.get();
            endDate = endDateOpt.get();
            if (endDate.isBefore(startDate)) {
                // Consider throwing IllegalArgumentException handled by GlobalExceptionHandler
                return ResponseEntity.badRequest().build(); // Or some error DTO
            }
        } else if (startDateOpt.isPresent()) { // Only start date provided
            startDate = startDateOpt.get();
            endDate = startDate.plusDays(6); // Default to a 7-day period
        } else if (endDateOpt.isPresent()) { // Only end date provided
            endDate = endDateOpt.get();
            startDate = endDate.minusDays(6); // Default to a 7-day period
        } else { // Neither provided, default to current week
            startDate = today.with(DayOfWeek.MONDAY);
            // If today is Sunday, MONDAY would be tomorrow. So, if today is Sunday, this week's Monday was 6 days ago.
            // If today is part of the week (Mon-Sat), it's fine.
            // A more common definition of "current week" might be:
            // startDate = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
            // endDate = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
            // For simplicity as per prompt's example using with(DayOfWeek.MONDAY/SUNDAY):
            if (today.getDayOfWeek() == DayOfWeek.SUNDAY) { // if today is Sunday, this week's Monday was 6 days ago
                 startDate = today.minusDays(6);
                 endDate = today;
            } else { // if today is Mon-Sat
                 startDate = today.with(DayOfWeek.MONDAY);
                 endDate = today.with(DayOfWeek.SUNDAY);
            }
        }

        logger.info("Fetching weekly summary for user {} from {} to {}", authUser.getUsername(), startDate, endDate);
        WeeklySummaryResponseDto summary = financialInsightService.getWeeklySummary(authUser.getId(), startDate, endDate);
        return ResponseEntity.ok(summary);
    }

    @Operation(summary = "Generate AI-powered financial insight", description = "Submits a user query along with their financial context to an LLM to receive personalized advice.") // Added
    @PostMapping("/generate")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<InsightResponseDto> generateInsight(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody InsightRequestDto insightRequestDto) {
        User authUser = getAuthenticatedUser(userDetails);
        logger.info("Generating LLM insight for user {} with query: '{}'", authUser.getUsername(), insightRequestDto.getUserQuery());
        InsightResponseDto insight = financialInsightService.generateLlmInsight(authUser.getId(), insightRequestDto.getUserQuery());
        return ResponseEntity.ok(insight);
    }
}
