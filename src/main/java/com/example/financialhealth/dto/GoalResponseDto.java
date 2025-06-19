package com.example.financialhealth.dto;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import lombok.AllArgsConstructor; // Added
import lombok.Data; // Added
import lombok.NoArgsConstructor; // Added

@Data // Lombok
@NoArgsConstructor // Lombok
@AllArgsConstructor // Lombok
public class GoalResponseDto {

    private Long id;
    private Long userId;
    private String goalName;
    private BigDecimal targetAmount;
    private BigDecimal currentAmount;
    private LocalDate targetDate;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    // Manual getters, setters, and constructors are removed.
}
