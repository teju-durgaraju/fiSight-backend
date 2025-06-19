package com.example.financialhealth.dto;

import com.example.financialhealth.model.enums.TransactionType; // Using enum directly for type safety
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import lombok.AllArgsConstructor; // Added
import lombok.Data; // Added
import lombok.NoArgsConstructor; // Added

/**
 * Data Transfer Object for sending transaction details to the client.
 * Conversion logic between Transaction Entity and this DTO is typically handled
 * in a service layer or a dedicated mapper component.
 */
@Data // Lombok
@NoArgsConstructor // Lombok
@AllArgsConstructor // Lombok
public class TransactionResponseDto {

    private Long id;
    private Long userId;
    private TransactionType type; // Using enum directly for better type safety on response
    private BigDecimal amount;
    private String categoryName; // Changed from category to categoryName
    private LocalDate transactionDate;
    private String description;
    private Timestamp createdAt;

    // Manual getters, setters, and constructors are removed.
}
