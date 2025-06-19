package com.example.financialhealth.dto.gemini;

import lombok.AllArgsConstructor; // Added
import lombok.Data; // Added
import lombok.NoArgsConstructor; // Added

@Data // Lombok
@NoArgsConstructor // Lombok
@AllArgsConstructor // Lombok
// Used for both request and response parts if structure is similar
public class GeminiPartDto {
    private String text;

    // Manual getters, setters, and constructors are removed.
}
