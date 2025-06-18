package com.example.financialhealth.controller;

import com.example.financialhealth.dto.BudgetRequestDto;
import com.example.financialhealth.dto.BudgetResponseDto;
import com.example.financialhealth.model.User;
import com.example.financialhealth.service.BudgetService;
import io.swagger.v3.oas.annotations.Operation; // Added
import io.swagger.v3.oas.annotations.tags.Tag; // Added
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Tag(name = "Budgets", description = "Manage monthly budgets for spending categories.") // Added
@RestController
@RequestMapping("/api/v1/budgets")
public class BudgetController {

    private static final Logger logger = LoggerFactory.getLogger(BudgetController.class);
    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    private User getAuthenticatedUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails instanceof User) {
            return (User) userDetails;
        }
        logger.error("User details are not of expected type (User). Actual type: {}", userDetails != null ? userDetails.getClass().getName() : "null");
        throw new InsufficientAuthenticationException("User details not found or not of expected type. Ensure you are authenticated correctly.");
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<BudgetResponseDto>> getBudgets(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Optional<String> month) { // month in YYYY-MM format
        User authUser = getAuthenticatedUser(userDetails);
        logger.info("Fetching budgets for user {} (month filter: {})", authUser.getUsername(), month.orElse("N/A"));
        List<BudgetResponseDto> budgets = budgetService.getBudgets(authUser.getId(), month);
        return ResponseEntity.ok(budgets);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<BudgetResponseDto> getBudgetById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        User authUser = getAuthenticatedUser(userDetails);
        logger.info("Fetching budget with id {} for user {}", id, authUser.getUsername());
        // Service will throw BudgetNotFoundException if not found or not owned, handled by GlobalExceptionHandler
        return budgetService.getBudgetByIdForUser(authUser.getId(), id)
                .map(ResponseEntity::ok)
                 // Should ideally not be reached if service throws an exception for not found
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @Operation(summary = "Create a new budget", description = "Sets a new budget for a specific category and month for the authenticated user.") // Added
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<BudgetResponseDto> createBudget(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody BudgetRequestDto requestDto) {
        User authUser = getAuthenticatedUser(userDetails);
        logger.info("Creating budget for user {} with category ID '{}' for month '{}'", // Corrected log
                authUser.getUsername(), requestDto.getCategoryId(), requestDto.getMonth());
        BudgetResponseDto createdBudget = budgetService.createBudget(authUser.getId(), requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdBudget);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<BudgetResponseDto> updateBudget(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody BudgetRequestDto requestDto) {
        User authUser = getAuthenticatedUser(userDetails);
        logger.info("Updating budget with id {} for user {}", id, authUser.getUsername());
        BudgetResponseDto updatedBudget = budgetService.updateBudget(authUser.getId(), id, requestDto);
        return ResponseEntity.ok(updatedBudget);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> deleteBudget(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        User authUser = getAuthenticatedUser(userDetails);
        logger.info("Deleting budget with id {} for user {}", id, authUser.getUsername());
        budgetService.deleteBudget(authUser.getId(), id);
        return ResponseEntity.noContent().build();
    }
}
