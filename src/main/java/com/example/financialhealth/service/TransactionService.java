package com.example.financialhealth.service;

import com.example.financialhealth.dto.TransactionRequestDto;
import com.example.financialhealth.dto.TransactionResponseDto;
import com.example.financialhealth.model.enums.TransactionType;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface TransactionService {
    TransactionResponseDto createTransaction(Long userId, TransactionRequestDto requestDto);
    List<TransactionResponseDto> getTransactionsForUser(Long userId, Optional<TransactionType> type, Optional<String> category, Optional<LocalDate> startDate, Optional<LocalDate> endDate);
    Optional<TransactionResponseDto> getTransactionByIdForUser(Long userId, Long transactionId);
    TransactionResponseDto updateTransaction(Long userId, Long transactionId, TransactionRequestDto requestDto);
    void deleteTransaction(Long userId, Long transactionId);
}
