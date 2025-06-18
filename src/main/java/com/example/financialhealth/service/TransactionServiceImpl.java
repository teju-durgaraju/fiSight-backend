package com.example.financialhealth.service;

import com.example.financialhealth.dto.TransactionRequestDto;
import com.example.financialhealth.dto.TransactionResponseDto;
import com.example.financialhealth.exception.TransactionNotFoundException;
import com.example.financialhealth.exception.UserNotFoundException;
import com.example.financialhealth.model.Transaction;
import com.example.financialhealth.model.User;
import com.example.financialhealth.model.enums.TransactionType;
import com.example.financialhealth.repository.TransactionRepository;
import com.example.financialhealth.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TransactionServiceImpl implements TransactionService {

    private static final Logger logger = LoggerFactory.getLogger(TransactionServiceImpl.class);

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;

    public TransactionServiceImpl(TransactionRepository transactionRepository, UserRepository userRepository) {
        this.transactionRepository = transactionRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public TransactionResponseDto createTransaction(Long userId, TransactionRequestDto requestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        Transaction transaction = new Transaction();
        mapToEntity(requestDto, user, transaction);
        Transaction savedTransaction = transactionRepository.save(transaction);
        logger.info("Created transaction with id {} for user {}", savedTransaction.getId(), userId);
        return mapToDto(savedTransaction);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponseDto> getTransactionsForUser(Long userId, Optional<TransactionType> typeFilter, Optional<String> categoryFilter, Optional<LocalDate> startDateFilter, Optional<LocalDate> endDateFilter) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));

        List<Transaction> transactions;

        if (startDateFilter.isPresent() && endDateFilter.isPresent()) {
            transactions = transactionRepository.findByUserAndTransactionDateBetween(user, startDateFilter.get(), endDateFilter.get());
        } else if (typeFilter.isPresent()) {
            // This will fetch all transactions of a certain type, then other filters can be applied via stream
            transactions = transactionRepository.findByUserAndType(user, typeFilter.get());
        } else if (categoryFilter.isPresent()) {
            // This will fetch all transactions of a certain category, then other filters can be applied via stream
            transactions = transactionRepository.findByUserAndCategoryIgnoreCase(user, categoryFilter.get());
        }
        else {
            transactions = transactionRepository.findByUserOrderByTransactionDateDesc(user);
        }

        // Apply remaining filters using streams if they were not the primary query path
        if (startDateFilter.isPresent() && endDateFilter.isPresent()) {
            // If date range was primary, other filters still need to be applied if present
            if (typeFilter.isPresent()) {
                transactions = transactions.stream().filter(t -> t.getType() == typeFilter.get()).collect(Collectors.toList());
            }
            if (categoryFilter.isPresent()) {
                transactions = transactions.stream().filter(t -> t.getCategory().equalsIgnoreCase(categoryFilter.get())).collect(Collectors.toList());
            }
        } else { // If date range was NOT primary
            if (typeFilter.isPresent() && !(transactions.isEmpty() && !transactionRepository.findByUserAndType(user, typeFilter.get()).isEmpty() )) {
                 // Apply if not already the primary filter
                 // Small correction: the condition above is a bit complex, simplifying stream filter
            }
            if (categoryFilter.isPresent() && !(transactions.isEmpty() && !transactionRepository.findByUserAndCategoryIgnoreCase(user, categoryFilter.get()).isEmpty())) {
                // Apply if not already the primary filter
            }
        }

        // Refined filtering logic: Fetch a base set then stream filter for additional criteria
        // For simplicity in this iteration, the above prioritizes one repo call.
        // A more robust version would fetch all by user, then stream, or use Specifications/QueryDSL.
        // For now, I'll stick to the single main filter path and then stream the rest.

        final List<Transaction> initialTransactions = transactionRepository.findByUserOrderByTransactionDateDesc(user);

        return initialTransactions.stream()
            .filter(t -> typeFilter.map(type -> t.getType() == type).orElse(true))
            .filter(t -> categoryFilter.map(cat -> t.getCategory().equalsIgnoreCase(cat)).orElse(true))
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
        mapToEntity(requestDto, transaction.getUser(), transaction); // User remains the same
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
                transaction.getCategory(),
                transaction.getTransactionDate(),
                transaction.getDescription(),
                transaction.getCreatedAt()
        );
    }

    private void mapToEntity(TransactionRequestDto dto, User user, Transaction transaction) {
        transaction.setUser(user);
        try {
            transaction.setType(TransactionType.valueOf(dto.getType().toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid transaction type: " + dto.getType() + ". Must be INCOME or EXPENSE.", e);
        }
        transaction.setAmount(dto.getAmount());
        transaction.setCategory(dto.getCategory());
        transaction.setTransactionDate(dto.getTransactionDate());
        transaction.setDescription(dto.getDescription());
        // createdAt is handled by @CreationTimestamp for new entities
        // and should not be updated for existing ones.
    }
}
