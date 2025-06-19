package com.example.financialhealth.dto.gemini;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor; // Added
import lombok.Data; // Added
import lombok.NoArgsConstructor; // Added

@Data // Lombok
@NoArgsConstructor // Lombok
@AllArgsConstructor // Lombok
// Add other fields like finishReason, safetyRatings if they need to be captured.
@JsonIgnoreProperties(ignoreUnknown = true) // Good practice for external API DTOs
public class GeminiCandidateDto {
    private GeminiContentDto content;

    // Manual getters, setters, and constructors are removed.
}
