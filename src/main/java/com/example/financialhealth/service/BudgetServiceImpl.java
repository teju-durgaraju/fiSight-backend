package com.example.financialhealth.service;

import com.example.financialhealth.dto.BudgetRequestDto;
import com.example.financialhealth.dto.BudgetResponseDto;
import com.example.financialhealth.exception.BudgetNotFoundException;
import com.example.financialhealth.exception.CategoryNotFoundException; // Added
import com.example.financialhealth.exception.DuplicateBudgetException;
import com.example.financialhealth.exception.UserNotFoundException;
import com.example.financialhealth.model.Budget;
import com.example.financialhealth.model.Category; // Added
import com.example.financialhealth.model.Transaction;
import com.example.financialhealth.model.User;
import com.example.financialhealth.model.enums.TransactionType;
import com.example.financialhealth.repository.BudgetRepository;
import com.example.financialhealth.repository.CategoryRepository; // Added
import com.example.financialhealth.repository.TransactionRepository;
import com.example.financialhealth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class BudgetServiceImpl implements BudgetService {

    private static final Logger logger = LoggerFactory.getLogger(BudgetServiceImpl.class);

    private final BudgetRepository budgetRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository; // Added

    public BudgetServiceImpl(BudgetRepository budgetRepository,
                             UserRepository userRepository,
                             TransactionRepository transactionRepository,
                             CategoryRepository categoryRepository) { // Added
        this.budgetRepository = budgetRepository;
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.categoryRepository = categoryRepository; // Added
    }

    @Override
    @Transactional
    public BudgetResponseDto createBudget(Long userId, BudgetRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        Category category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with ID: " + requestDto.getCategoryId()));

        budgetRepository.findByUserAndCategoryAndMonth(user, category, requestDto.getMonth())
                .ifPresent(existingBudget -> {
                    throw new DuplicateBudgetException(
                            "Budget already exists for user " + userId +
                                    ", category '" + category.getName() + // Use category name for message
                                    "', and month '" + requestDto.getMonth() + "'."
                    );
                });

        Budget budget = new Budget();
        mapToEntity(requestDto, user, category, budget); // Pass Category object
        Budget savedBudget = budgetRepository.save(budget);
        logger.info("Created budget with id {} for user {}, category '{}', month '{}'",
                savedBudget.getId(), userId, savedBudget.getCategory().getName(), savedBudget.getMonth());
        return mapToDto(savedBudget);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BudgetResponseDto> getBudgets(Long userId, Optional<String> monthFilter) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        List<Budget> budgets;
        if (monthFilter.isPresent()) {
            budgets = budgetRepository.findByUserAndMonthOrderByCategoryAsc(user, monthFilter.get());
        } else {
            budgets = budgetRepository.findByUserOrderByMonthDescCategoryAsc(user);
        }
        return budgets.stream().map(this::mapToDto).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BudgetResponseDto> getBudgetByIdForUser(Long userId, Long budgetId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        return budgetRepository.findByIdAndUser(budgetId, user)
                .map(this::mapToDto);
    }

    @Override
    @Transactional
    public BudgetResponseDto updateBudget(Long userId, Long budgetId, BudgetRequestDto requestDto) {
        Budget budget = findBudgetByIdAndUserOrThrow(budgetId, userId);
        User user = budget.getUser(); // User must remain the same for a given budget item

        Category category = budget.getCategory();
        if (requestDto.getCategoryId() != null && !requestDto.getCategoryId().equals(category.getId())) {
            category = categoryRepository.findById(requestDto.getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException("Category not found with ID: " + requestDto.getCategoryId()));
        }

        // Check for duplicate with new category and month before setting
        // The month is also part of the DTO, so use requestDto.getMonth()
        final Category finalCategory = category; // for lambda
        budgetRepository.findByUserAndCategoryAndMonth(user, finalCategory, requestDto.getMonth())
            .filter(existingBudget -> !existingBudget.getId().equals(budgetId)) // ensure it's not the same budget item
            .ifPresent(existingBudget -> {
                throw new DuplicateBudgetException(
                        "A budget for category '" + finalCategory.getName() +
                        "' and month '" + requestDto.getMonth() + "' already exists."
                );
            });

        mapToEntity(requestDto, user, finalCategory, budget); // Pass updated category

        Budget updatedBudget = budgetRepository.save(budget);
        logger.info("Updated budget with id {} for user {}", updatedBudget.getId(), userId);
        return mapToDto(updatedBudget);
    }

    @Override
    @Transactional
    public void deleteBudget(Long userId, Long budgetId) {
        Budget budget = findBudgetByIdAndUserOrThrow(budgetId, userId);
        budgetRepository.delete(budget);
        logger.info("Deleted budget with id {} for user {}", budgetId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal calculateCurrentSpending(Long userId, String categoryName, String month) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        Category category = categoryRepository.findByNameIgnoreCase(categoryName)
                .orElse(null);

        if (category == null) {
            logger.warn("Category '{}' not found for spending calculation for user ID {}. Returning 0 spending.", categoryName, userId);
            return BigDecimal.ZERO;
        }

        YearMonth yearMonth;
        try {
            yearMonth = YearMonth.parse(month); // Assumes month is "YYYY-MM"
        } catch (java.time.format.DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid month format. Please use YYYY-MM. Provided: " + month, e);
        }

        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // Use the repository method that accepts a Category object
        List<Transaction> transactions = transactionRepository.findByUserAndCategoryAndTypeAndTransactionDateBetween(
                user, category, TransactionType.EXPENSE, startDate, endDate
        );

        return transactions.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    // --- Private Helper Methods ---

    private Budget findBudgetByIdAndUserOrThrow(Long budgetId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId + ". Cannot process budget operation."));
        return budgetRepository.findByIdAndUser(budgetId, user)
                .orElseThrow(() -> new BudgetNotFoundException("Budget not found with id: " + budgetId + " for user: " + userId));
    }

    private BudgetResponseDto mapToDto(Budget budget) {
        if (budget == null) return null;
        return new BudgetResponseDto(
                budget.getId(),
                budget.getUser().getId(),
                budget.getCategory().getName(), // Get name from Category entity
                budget.getAllocatedAmount(),
                budget.getMonth(),
                budget.getTotalMonthlyBudgetGoal(),
                budget.getCreatedAt(),
                budget.getUpdatedAt()
        );
    }

    // Updated signature to include Category
    private void mapToEntity(BudgetRequestDto dto, User user, Category category, Budget budget) {
        budget.setUser(user);
        budget.setCategory(category); // Set Category entity
        budget.setAllocatedAmount(dto.getAllocatedAmount());
        budget.setMonth(dto.getMonth());
        budget.setTotalMonthlyBudgetGoal(dto.getTotalMonthlyBudgetGoal());
        // createdAt and updatedAt are handled by @CreationTimestamp and @UpdateTimestamp
    }
}
