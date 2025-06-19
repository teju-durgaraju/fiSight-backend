package com.example.financialhealth.repository;

import com.example.financialhealth.model.Category;
import com.example.financialhealth.model.Transaction;
import com.example.financialhealth.model.User;
import com.example.financialhealth.model.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query; // Added
import org.springframework.data.repository.query.Param; // Added
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUserOrderByTransactionDateDesc(User user);

    List<Transaction> findByUserAndTransactionDateBetween(User user, LocalDate startDate, LocalDate endDate);

    List<Transaction> findByUserAndType(User user, TransactionType type);

    List<Transaction> findByUserAndCategory(User user, Category category); // Changed from String category

    Optional<Transaction> findByIdAndUser(Long id, User user);

    List<Transaction> findByUserAndCategoryAndTypeAndTransactionDateBetween( // Changed from String category
            User user, Category category, TransactionType type, LocalDate startDate, LocalDate endDate
    );

    @Query("SELECT t FROM Transaction t JOIN FETCH t.category WHERE t.user = :user AND t.transactionDate BETWEEN :startDate AND :endDate ORDER BY t.transactionDate DESC")
    List<Transaction> findByUserAndTransactionDateBetweenWithCategory(
        @Param("user") User user,
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );
}
