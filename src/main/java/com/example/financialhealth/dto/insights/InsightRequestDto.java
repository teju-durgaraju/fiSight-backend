package com.example.financialhealth.dto.insights;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor; // Added
import lombok.Data; // Added
import lombok.NoArgsConstructor; // Added

@Data // Lombok
@NoArgsConstructor // Lombok
@AllArgsConstructor // Lombok
public class InsightRequestDto {

    @NotBlank(message = "User query cannot be blank.")
    @Schema(description = "User's question or topic for financial advice.", example = "How can I save more money?")
    private String userQuery;

    // Manual getters, setters, and constructors are removed.
}
