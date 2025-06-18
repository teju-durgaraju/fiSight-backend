package com.example.financialhealth.dto.gemini;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true) // Crucial for external API DTOs
public class GeminiResponseDto {
    private List<GeminiCandidateDto> candidates;

    public GeminiResponseDto() {}

    public GeminiResponseDto(List<GeminiCandidateDto> candidates) {
        this.candidates = candidates;
    }

    public List<GeminiCandidateDto> getCandidates() {
        return candidates;
    }

    public void setCandidates(List<GeminiCandidateDto> candidates) {
        this.candidates = candidates;
    }
}
