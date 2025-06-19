package com.example.financialhealth.dto.gemini;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;
import lombok.AllArgsConstructor; // Added
import lombok.Data; // Added
import lombok.NoArgsConstructor; // Added

@Data // Lombok
@NoArgsConstructor // Lombok
@AllArgsConstructor // Lombok
@JsonIgnoreProperties(ignoreUnknown = true) // Crucial for external API DTOs
public class GeminiResponseDto {
    private List<GeminiCandidateDto> candidates;

    // Manual getters, setters, and constructors are removed.
}
