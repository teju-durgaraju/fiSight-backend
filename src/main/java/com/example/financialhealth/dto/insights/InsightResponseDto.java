package com.example.financialhealth.dto.insights;

import lombok.AllArgsConstructor; // Added
import lombok.Data; // Added
import lombok.NoArgsConstructor; // Added

@Data // Lombok
@NoArgsConstructor // Lombok
@AllArgsConstructor // Lombok
public class InsightResponseDto {
    private String adviceText;

    // Manual getters, setters, and constructors are removed.
}
