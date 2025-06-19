package com.example.financialhealth.dto.insights;

import java.math.BigDecimal;
import lombok.AllArgsConstructor; // Added
import lombok.Data; // Added
import lombok.NoArgsConstructor; // Added

@Data // Lombok
@NoArgsConstructor // Lombok
@AllArgsConstructor // Lombok
public class CategorySpendingDto {
    private String category;
    private BigDecimal amount;

    // Manual getters, setters, and constructors are removed.
}
