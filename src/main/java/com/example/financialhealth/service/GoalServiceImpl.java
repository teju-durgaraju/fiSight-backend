package com.example.financialhealth.service;

import com.example.financialhealth.dto.GoalRequestDto;
import com.example.financialhealth.dto.GoalResponseDto;
import com.example.financialhealth.exception.GoalNotFoundException;
import com.example.financialhealth.exception.UserNotFoundException; // Re-using from previous subtasks
import com.example.financialhealth.model.Goal;
import com.example.financialhealth.model.User;
import com.example.financialhealth.repository.GoalRepository;
import com.example.financialhealth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class GoalServiceImpl implements GoalService {

    private static final Logger logger = LoggerFactory.getLogger(GoalServiceImpl.class);

    private final GoalRepository goalRepository;
    private final UserRepository userRepository;

    public GoalServiceImpl(GoalRepository goalRepository, UserRepository userRepository) {
        this.goalRepository = goalRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public GoalResponseDto createGoal(Long userId, GoalRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        Goal goal = new Goal();
        mapToEntity(requestDto, user, goal);
        // Ensure currentAmount is not null, default to ZERO if DTO provides null
        if (requestDto.getCurrentAmount() == null) {
            goal.setCurrentAmount(BigDecimal.ZERO);
        } else {
            goal.setCurrentAmount(requestDto.getCurrentAmount());
        }
        Goal savedGoal = goalRepository.save(goal);
        logger.info("Created goal with id {} for user {}", savedGoal.getId(), userId);
        return mapToDto(savedGoal);
    }

    @Override
    @Transactional(readOnly = true)
    public List<GoalResponseDto> getGoals(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        List<Goal> goals = goalRepository.findByUserOrderByTargetDateAscGoalNameAsc(user);
        return goals.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<GoalResponseDto> getGoalByIdForUser(Long userId, Long goalId) {
        // User fetching is implicitly handled by findGoalByIdAndUserOrThrow or repository method
        return goalRepository.findByIdAndUser(goalId, userRepository.findById(userId)
                        .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId)))
                .map(this::mapToDto);
    }

    @Override
    @Transactional
    public GoalResponseDto updateGoal(Long userId, Long goalId, GoalRequestDto requestDto) {
        Goal goal = findGoalByIdAndUserOrThrow(goalId, userId);
        mapToEntity(requestDto, goal.getUser(), goal); // User remains the same
        // currentAmount can be updated
        if (requestDto.getCurrentAmount() != null) {
             goal.setCurrentAmount(requestDto.getCurrentAmount());
        }
        // If currentAmount is not in DTO, it means no update to it, so existing value is kept.
        // If it should be resettable to zero if not provided, logic would be:
        // goal.setCurrentAmount(requestDto.getCurrentAmount() == null ? goal.getCurrentAmount() : requestDto.getCurrentAmount());
        // But typically, if a value is in DTO, it's an explicit update. If not, field is not touched.
        // The DTO has currentAmount as optional, so if it's null, we DON'T update it from DTO.
        // If it's non-null in DTO, we update.

        Goal updatedGoal = goalRepository.save(goal);
        logger.info("Updated goal with id {} for user {}", updatedGoal.getId(), userId);
        return mapToDto(updatedGoal);
    }

    @Override
    @Transactional
    public void deleteGoal(Long userId, Long goalId) {
        Goal goal = findGoalByIdAndUserOrThrow(goalId, userId);
        goalRepository.delete(goal);
        logger.info("Deleted goal with id {} for user {}", goalId, userId);
    }

    // --- Private Helper Methods ---

    private Goal findGoalByIdAndUserOrThrow(Long goalId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId + ". Cannot process goal operation."));
        return goalRepository.findByIdAndUser(goalId, user)
                .orElseThrow(() -> new GoalNotFoundException("Goal not found with id: " + goalId + " for user: " + userId));
    }

    private GoalResponseDto mapToDto(Goal goal) {
        if (goal == null) return null;
        return new GoalResponseDto(
                goal.getId(),
                goal.getUser().getId(),
                goal.getGoalName(),
                goal.getTargetAmount(),
                goal.getCurrentAmount(),
                goal.getTargetDate(),
                goal.getCreatedAt(),
                goal.getUpdatedAt()
        );
    }

    private void mapToEntity(GoalRequestDto dto, User user, Goal goal) {
        goal.setUser(user);
        goal.setGoalName(dto.getGoalName());
        goal.setTargetAmount(dto.getTargetAmount());
        // currentAmount is handled in create/update methods explicitly based on DTO presence
        if (dto.getCurrentAmount() != null) { // Only set if present in DTO
            goal.setCurrentAmount(dto.getCurrentAmount());
        } else if (goal.getId() == null) { // For new goals, if not in DTO, default to ZERO
             goal.setCurrentAmount(BigDecimal.ZERO);
        }
        // If updating and currentAmount is null in DTO, we keep the existing currentAmount.

        goal.setTargetDate(dto.getTargetDate());
        // createdAt and updatedAt are handled by @CreationTimestamp and @UpdateTimestamp
    }
}
