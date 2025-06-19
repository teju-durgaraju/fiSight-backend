package com.example.financialhealth.dto.gemini;

import java.util.List;
import lombok.AllArgsConstructor; // Added
import lombok.Data; // Added
import lombok.NoArgsConstructor; // Added

@Data // Lombok
@NoArgsConstructor // Lombok
@AllArgsConstructor // Lombok
public class GeminiRequestDto {
    private List<GeminiContentDto> contents;

    // Manual getters, setters, and constructors are removed.
}
