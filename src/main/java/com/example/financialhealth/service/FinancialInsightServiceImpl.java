package com.example.financialhealth.service;

import com.example.financialhealth.config.GeminiApiConfig;
import com.example.financialhealth.dto.gemini.GeminiCandidateDto;
import com.example.financialhealth.dto.gemini.GeminiContentDto;
import com.example.financialhealth.dto.gemini.GeminiPartDto;
import com.example.financialhealth.dto.gemini.GeminiRequestDto;
import com.example.financialhealth.dto.gemini.GeminiResponseDto;
import com.example.financialhealth.dto.insights.CategorySpendingDto;
import com.example.financialhealth.dto.insights.InsightResponseDto;
import com.example.financialhealth.dto.insights.WeeklySummaryResponseDto;
import com.example.financialhealth.exception.ExternalServiceException;
import com.example.financialhealth.exception.UserNotFoundException;
import com.example.financialhealth.model.Budget;
import com.example.financialhealth.model.Goal;
import com.example.financialhealth.model.Transaction;
import com.example.financialhealth.model.User;
import com.example.financialhealth.model.enums.TransactionType;
import com.example.financialhealth.repository.BudgetRepository;
import com.example.financialhealth.repository.GoalRepository;
import com.example.financialhealth.repository.TransactionRepository;
import com.example.financialhealth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FinancialInsightServiceImpl implements FinancialInsightService {

    private static final Logger logger = LoggerFactory.getLogger(FinancialInsightServiceImpl.class);

    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final GoalRepository goalRepository;
    private final GeminiApiConfig geminiApiConfig;
    private final RestTemplate restTemplate;

    public FinancialInsightServiceImpl(UserRepository userRepository,
                                     TransactionRepository transactionRepository,
                                     BudgetRepository budgetRepository,
                                     GoalRepository goalRepository,
                                     GeminiApiConfig geminiApiConfig,
                                     RestTemplate restTemplate) { // Added RestTemplate
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.budgetRepository = budgetRepository;
        this.goalRepository = goalRepository;
        this.geminiApiConfig = geminiApiConfig;
        this.restTemplate = restTemplate; // Assign injected RestTemplate
    }

    @Override
    @Transactional(readOnly = true)
    public WeeklySummaryResponseDto getWeeklySummary(Long userId, LocalDate startDate, LocalDate endDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        // Use the new method with JOIN FETCH
        List<Transaction> transactions = transactionRepository.findByUserAndTransactionDateBetweenWithCategory(user, startDate, endDate);

        BigDecimal totalIncome = BigDecimal.ZERO;
        BigDecimal totalExpenses = BigDecimal.ZERO;
        Map<String, BigDecimal> spendingByCategoryMap = new HashMap<>();

        for (Transaction t : transactions) {
            if (t.getType() == TransactionType.INCOME) {
                totalIncome = totalIncome.add(t.getAmount());
            } else if (t.getType() == TransactionType.EXPENSE) {
                totalExpenses = totalExpenses.add(t.getAmount());
                // Ensure category is not null before getting its name
                if (t.getCategory() != null && t.getCategory().getName() != null) {
                    spendingByCategoryMap.merge(t.getCategory().getName(), t.getAmount(), BigDecimal::add);
                } else {
                    // Handle cases where category might be unexpectedly null, though schema says it's not optional
                    logger.warn("Transaction with id {} has a null category or category name.", t.getId());
                    spendingByCategoryMap.merge("Uncategorized", t.getAmount(), BigDecimal::add);
                }
            }
        }

        BigDecimal netSavings = totalIncome.subtract(totalExpenses);
        List<CategorySpendingDto> spendingByCategoryList = spendingByCategoryMap.entrySet().stream()
                .map(entry -> new CategorySpendingDto(entry.getKey(), entry.getValue()))
                .sorted((c1, c2) -> c2.getAmount().compareTo(c1.getAmount())) // Sort by amount desc
                .collect(Collectors.toList());

        logger.info("Generated weekly summary for user {} from {} to {}", userId, startDate, endDate);
        return new WeeklySummaryResponseDto(startDate, endDate, totalIncome, totalExpenses, netSavings, spendingByCategoryList);
    }

    @Override
    @Transactional(readOnly = true)
    public InsightResponseDto generateLlmInsight(Long userId, String userQuery) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        // Fetch financial data
        LocalDate transactionsEndDate = LocalDate.now();
        LocalDate transactionsStartDate = transactionsEndDate.minusDays(60);
        // Use the new method with JOIN FETCH
        List<Transaction> recentTransactions = transactionRepository.findByUserAndTransactionDateBetweenWithCategory(user, transactionsStartDate, transactionsEndDate);

        String currentMonthStr = YearMonth.now().toString(); // YYYY-MM
        List<Budget> currentBudgets = budgetRepository.findByUserAndMonthOrderByCategoryAsc(user, currentMonthStr);

        List<Goal> activeGoals = goalRepository.findByUserOrderByTargetDateAscGoalNameAsc(user);

        // Construct Prompt String
        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append("You are a precise and helpful financial advisor.\n");
        promptBuilder.append("The user has asked the following question: '").append(userQuery).append("'\n\n");
        promptBuilder.append("Here is some relevant financial data for the user:\n");

        // Transactions Summary
        if (recentTransactions.isEmpty()) {
            promptBuilder.append("- No transactions recorded in the last 60 days.\n");
        } else {
            BigDecimal last60DaysIncome = recentTransactions.stream().filter(t -> t.getType() == TransactionType.INCOME).map(Transaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal last60DaysExpenses = recentTransactions.stream().filter(t -> t.getType() == TransactionType.EXPENSE).map(Transaction::getAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            promptBuilder.append("- Recent Transactions (last 60 days): Total Income: $").append(last60DaysIncome)
                         .append(", Total Expenses: $").append(last60DaysExpenses).append(".\n");
            // Optionally, list top 3-5 expense categories or a few large transactions if needed
        }

        // Budgets Summary
        if (currentBudgets.isEmpty()) {
            promptBuilder.append("- No budgets set for the current month (").append(currentMonthStr).append(").\n");
        } else {
            promptBuilder.append("- Budgets for ").append(currentMonthStr).append(":\n");
            currentBudgets.forEach(b -> {
                String categoryName = (b.getCategory() != null && b.getCategory().getName() != null) ? b.getCategory().getName() : "Uncategorized";
                promptBuilder.append("  - Category '").append(categoryName)
                             .append("': Allocated $").append(b.getAllocatedAmount()).append(".\n");
            });
        }

        // Goals Summary
        if (activeGoals.isEmpty()) {
            promptBuilder.append("- No financial goals currently set.\n");
        } else {
            promptBuilder.append("- Active Financial Goals:\n");
            activeGoals.forEach(g -> promptBuilder.append("  - Goal '").append(g.getGoalName())
                               .append("': Target $").append(g.getTargetAmount())
                               .append(", Current $").append(g.getCurrentAmount())
                               .append(g.getTargetDate() != null ? ", Target Date: " + g.getTargetDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : "")
                               .append(".\n"));
        }

        promptBuilder.append("\nBased on this information and the user's question, provide concise financial advice. Focus on actionable steps if applicable.");
        String promptText = promptBuilder.toString();
        logger.debug("Generated LLM prompt for user {}: {}", userId, promptText);

        // Prepare Gemini API Request DTO
        GeminiPartDto part = new GeminiPartDto(promptText);
        GeminiContentDto content = new GeminiContentDto(Collections.singletonList(part));
        // content.setRole("user"); // Gemini API usually infers this if it's the first/only content block
        GeminiRequestDto geminiRequest = new GeminiRequestDto(Collections.singletonList(content));

        // Call Gemini API
        String apiUrl = geminiApiConfig.getUrl() + "?key=" + geminiApiConfig.getKey();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<GeminiRequestDto> entity = new HttpEntity<>(geminiRequest, headers);

        try {
            logger.info("Calling Gemini API for user {}", userId);
            ResponseEntity<GeminiResponseDto> response = restTemplate.postForEntity(apiUrl, entity, GeminiResponseDto.class);

            String extractedText = "No advice generated.";
            if (response.getBody() != null && response.getBody().getCandidates() != null && !response.getBody().getCandidates().isEmpty()) {
                GeminiCandidateDto firstCandidate = response.getBody().getCandidates().get(0);
                if (firstCandidate.getContent() != null && firstCandidate.getContent().getParts() != null && !firstCandidate.getContent().getParts().isEmpty()) {
                    extractedText = firstCandidate.getContent().getParts().get(0).getText();
                }
            }
            logger.info("Successfully received insight from Gemini API for user {}", userId);
            return new InsightResponseDto(extractedText.trim());

        } catch (RestClientException e) {
            logger.error("Error calling Gemini API for user {}: {}", userId, e.getMessage(), e);
            throw new ExternalServiceException("Failed to get insight from LLM: " + e.getMessage(), e);
        }
    }
}
