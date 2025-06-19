package com.example.financialhealth.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;
import lombok.AllArgsConstructor; // Added
import lombok.Data; // Added
import lombok.NoArgsConstructor; // Added

@Data // Lombok
@NoArgsConstructor // Lombok
@AllArgsConstructor // Lombok
public class BudgetResponseDto {

    private Long id;
    private Long userId;
    private String categoryName; // Changed from category to categoryName
    private BigDecimal allocatedAmount;
    private String month; // YYYY-MM format
    private BigDecimal totalMonthlyBudgetGoal;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Manual getters, setters, and constructors are removed.
}
