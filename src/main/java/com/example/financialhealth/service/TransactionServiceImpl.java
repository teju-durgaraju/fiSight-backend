package com.example.financialhealth.service;

import com.example.financialhealth.dto.TransactionRequestDto;
import com.example.financialhealth.dto.TransactionResponseDto;
import com.example.financialhealth.exception.CategoryNotFoundException; // Added
import com.example.financialhealth.exception.TransactionNotFoundException;
import com.example.financialhealth.exception.UserNotFoundException;
import com.example.financialhealth.model.Category; // Added
import com.example.financialhealth.model.Transaction;
import com.example.financialhealth.model.User;
import com.example.financialhealth.model.enums.TransactionType;
import com.example.financialhealth.repository.CategoryRepository; // Added
import com.example.financialhealth.repository.TransactionRepository;
import com.example.financialhealth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Collections; // Added
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository; // Added

    public TransactionServiceImpl(TransactionRepository transactionRepository,
                                UserRepository userRepository,
                                CategoryRepository categoryRepository) { // Added
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository; // Added
    }

    @Override
    @Transactional
    public TransactionResponseDto createTransaction(Long userId, TransactionRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        Category category = categoryRepository.findById(requestDto.getCategoryId())
                .orElseThrow(() -> new CategoryNotFoundException("Category not found with ID: " + requestDto.getCategoryId()));
        Transaction transaction = new Transaction();
        mapToEntity(requestDto, user, category, transaction); // Pass category
        Transaction savedTransaction = transactionRepository.save(transaction);
        logger.info("Created transaction with id {} for user {}", savedTransaction.getId(), userId);
        return mapToDto(savedTransaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponseDto> getTransactionsForUser(Long userId, Optional<TransactionType> typeFilter, Optional<String> categoryFilter, Optional<LocalDate> startDateFilter, Optional<LocalDate> endDateFilter) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        // Resolve category name to Category entity for repository querying if category filter is present
        Optional<Category> categoryEntityOpt = categoryFilter.flatMap(name -> categoryRepository.findByNameIgnoreCase(name));

        // If category name is provided but not found, return empty list as no transactions can match.
        if (categoryFilter.isPresent() && !categoryEntityOpt.isPresent()) {
            return Collections.emptyList();
        }

        // Simplified logic: Fetch all by user, then filter.
        // More optimized would be to use specific repo methods if only one filter is present,
        // or Specifications/QueryDSL for multiple filters.
        // For this refactoring, focusing on making category work with existing stream approach.
        List<Transaction> initialTransactions = transactionRepository.findByUserOrderByTransactionDateDesc(user);

        return initialTransactions.stream()
                .filter(t -> typeFilter.map(type -> t.getType() == type).orElse(true))
                .filter(t -> categoryEntityOpt.map(catEntity -> t.getCategory().equals(catEntity)).orElse(true)) // Filter by Category object
                .filter(t -> startDateFilter.map(sd -> !t.getTransactionDate().isBefore(sd)).orElse(true))
                .filter(t -> endDateFilter.map(ed -> !t.getTransactionDate().isAfter(ed)).orElse(true))
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TransactionResponseDto> getTransactionByIdForUser(Long userId, Long transactionId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        return transactionRepository.findByIdAndUser(transactionId, user)
                .map(this::mapToDto);
    }

    @Override
    @Transactional
    public TransactionResponseDto updateTransaction(Long userId, Long transactionId, TransactionRequestDto requestDto) {
        Transaction transaction = findTransactionByIdAndUserOrThrow(transactionId, userId);
        User user = transaction.getUser(); // User remains the same

        Category category = transaction.getCategory(); // Keep existing category by default
        if (requestDto.getCategoryId() != null && !requestDto.getCategoryId().equals(category.getId())) {
            category = categoryRepository.findById(requestDto.getCategoryId())
                    .orElseThrow(() -> new CategoryNotFoundException("Category not found with ID: " + requestDto.getCategoryId()));
        }

        mapToEntity(requestDto, user, category, transaction); // Pass category
        Transaction updatedTransaction = transactionRepository.save(transaction);
        logger.info("Updated transaction with id {} for user {}", updatedTransaction.getId(), userId);
        return mapToDto(updatedTransaction);
    }

    @Override
    @Transactional
    public void deleteTransaction(Long userId, Long transactionId) {
        Transaction transaction = findTransactionByIdAndUserOrThrow(transactionId, userId);
        transactionRepository.delete(transaction);
        logger.info("Deleted transaction with id {} for user {}", transactionId, userId);
    }

    // --- Private Helper Methods ---

    private Transaction findTransactionByIdAndUserOrThrow(Long transactionId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId + ". Cannot process transaction operation."));
        return transactionRepository.findByIdAndUser(transactionId, user)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction not found with id: " + transactionId + " for user: " + userId));
    }

    private TransactionResponseDto mapToDto(Transaction transaction) {
        if (transaction == null) return null;
        return new TransactionResponseDto(
                transaction.getId(),
                transaction.getUser().getId(),
                transaction.getType(),
                transaction.getAmount(),
                transaction.getCategory().getName(), // Get name from Category entity
                transaction.getTransactionDate(),
                transaction.getDescription(),
                transaction.getCreatedAt()
        );
    }

    // Updated signature to include Category
    private void mapToEntity(TransactionRequestDto dto, User user, Category category, Transaction transaction) {
        transaction.setUser(user);
        transaction.setCategory(category); // Set Category entity
        try {
            transaction.setType(TransactionType.valueOf(dto.getType().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid transaction type: " + dto.getType() + ". Must be INCOME or EXPENSE.", e);
        }
        transaction.setAmount(dto.getAmount());
        transaction.setTransactionDate(dto.getTransactionDate());
        transaction.setDescription(dto.getDescription());
        // createdAt is handled by @CreationTimestamp for new entities
        // and should not be updated for existing ones.
    }
}
