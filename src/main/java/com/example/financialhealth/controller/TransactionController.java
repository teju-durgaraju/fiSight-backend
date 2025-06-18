package com.example.financialhealth.controller;

import com.example.financialhealth.dto.TransactionRequestDto;
import com.example.financialhealth.dto.TransactionResponseDto;
import com.example.financialhealth.model.User;
import com.example.financialhealth.model.enums.TransactionType;
import com.example.financialhealth.service.TransactionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionController {

    private static final Logger logger = LoggerFactory.getLogger(TransactionController.class);
    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    private User getAuthenticatedUser(@AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails instanceof User) {
            return (User) userDetails;
        }
        // This case should ideally not be reached if security is configured correctly
        // and CustomUserDetailsService returns our User object.
        logger.error("User details are not of expected type (User). Actual type: {}", userDetails != null ? userDetails.getClass().getName() : "null");
        throw new InsufficientAuthenticationException("User details not found or not of expected type. Ensure you are authenticated correctly.");
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<TransactionResponseDto>> getTransactions(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Optional<String> type,
            @RequestParam Optional<String> category,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Optional<LocalDate> endDate) {

        User authUser = getAuthenticatedUser(userDetails);
        logger.info("Fetching transactions for user {}", authUser.getUsername());

        Optional<TransactionType> typeEnumOptional = type.map(s -> {
            try {
                return TransactionType.valueOf(s.toUpperCase());
            } catch (IllegalArgumentException e) {
                // Consider throwing a custom bad request exception or logging and returning empty
                logger.warn("Invalid transaction type string provided: {}", s);
                throw new IllegalArgumentException("Invalid transaction type provided: " + s + ". Must be INCOME or EXPENSE.");
            }
        });

        List<TransactionResponseDto> transactions = transactionService.getTransactionsForUser(
                authUser.getId(), typeEnumOptional, category, startDate, endDate);
        return ResponseEntity.ok(transactions);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<TransactionResponseDto> getTransactionById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {

        User authUser = getAuthenticatedUser(userDetails);
        logger.info("Fetching transaction with id {} for user {}", id, authUser.getUsername());

        // The service method now returns Optional<TransactionResponseDto>
        // TransactionNotFoundException will be thrown by the service if not found,
        // and handled by GlobalExceptionHandler.
        return transactionService.getTransactionByIdForUser(authUser.getId(), id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build()); // Should not be reached if service throws
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<TransactionResponseDto> createTransaction(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody TransactionRequestDto requestDto) {

        User authUser = getAuthenticatedUser(userDetails);
        logger.info("Creating transaction for user {}", authUser.getUsername());
        TransactionResponseDto createdTransaction = transactionService.createTransaction(authUser.getId(), requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTransaction);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<TransactionResponseDto> updateTransaction(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequestDto requestDto) {

        User authUser = getAuthenticatedUser(userDetails);
        logger.info("Updating transaction with id {} for user {}", id, authUser.getUsername());
        TransactionResponseDto updatedTransaction = transactionService.updateTransaction(authUser.getId(), id, requestDto);
        return ResponseEntity.ok(updatedTransaction);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> deleteTransaction(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {

        User authUser = getAuthenticatedUser(userDetails);
        logger.info("Deleting transaction with id {} for user {}", id, authUser.getUsername());
        transactionService.deleteTransaction(authUser.getId(), id);
        return ResponseEntity.noContent().build();
    }
}
