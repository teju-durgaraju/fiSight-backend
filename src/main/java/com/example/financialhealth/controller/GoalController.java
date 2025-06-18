package com.example.financialhealth.controller;

import com.example.financialhealth.dto.GoalRequestDto;
import com.example.financialhealth.dto.GoalResponseDto;
import com.example.financialhealth.model.User;
import com.example.financialhealth.service.GoalService;
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

@RestController
@RequestMapping("/api/v1/goals")
public class GoalController {

    private static final Logger logger = LoggerFactory.getLogger(GoalController.class);
    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
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
    public ResponseEntity<List<GoalResponseDto>> getGoals(
            @AuthenticationPrincipal UserDetails userDetails) {
        User authUser = getAuthenticatedUser(userDetails);
        logger.info("Fetching goals for user {}", authUser.getUsername());
        List<GoalResponseDto> goals = goalService.getGoals(authUser.getId());
        return ResponseEntity.ok(goals);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<GoalResponseDto> getGoalById(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        User authUser = getAuthenticatedUser(userDetails);
        logger.info("Fetching goal with id {} for user {}", id, authUser.getUsername());
        // Service will throw GoalNotFoundException if not found or not owned, handled by GlobalExceptionHandler
        return goalService.getGoalByIdForUser(authUser.getId(), id)
                .map(ResponseEntity::ok)
                // Should ideally not be reached if service throws an exception for not found
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<GoalResponseDto> createGoal(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody GoalRequestDto requestDto) {
        User authUser = getAuthenticatedUser(userDetails);
        logger.info("Creating goal '{}' for user {}", requestDto.getGoalName(), authUser.getUsername());
        GoalResponseDto createdGoal = goalService.createGoal(authUser.getId(), requestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdGoal);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<GoalResponseDto> updateGoal(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id,
            @Valid @RequestBody GoalRequestDto requestDto) {
        User authUser = getAuthenticatedUser(userDetails);
        logger.info("Updating goal with id {} for user {}", id, authUser.getUsername());
        GoalResponseDto updatedGoal = goalService.updateGoal(authUser.getId(), id, requestDto);
        return ResponseEntity.ok(updatedGoal);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> deleteGoal(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long id) {
        User authUser = getAuthenticatedUser(userDetails);
        logger.info("Deleting goal with id {} for user {}", id, authUser.getUsername());
        goalService.deleteGoal(authUser.getId(), id);
        return ResponseEntity.noContent().build();
    }
}
