package com.example.financialhealth.service;

import com.example.financialhealth.dto.GoalRequestDto;
import com.example.financialhealth.dto.GoalResponseDto;
import java.util.List;
import java.util.Optional;

public interface GoalService {
    GoalResponseDto createGoal(Long userId, GoalRequestDto requestDto);
    List<GoalResponseDto> getGoals(Long userId);
    Optional<GoalResponseDto> getGoalByIdForUser(Long userId, Long goalId);
    GoalResponseDto updateGoal(Long userId, Long goalId, GoalRequestDto requestDto);
    void deleteGoal(Long userId, Long goalId);
}
