package com.example.financialhealth.dto.gemini;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

// Add other fields like finishReason, safetyRatings if they need to be captured.
@JsonIgnoreProperties(ignoreUnknown = true) // Good practice for external API DTOs
public class GeminiCandidateDto {
    private GeminiContentDto content;

    public GeminiCandidateDto() {}

    public GeminiCandidateDto(GeminiContentDto content) {
        this.content = content;
    }

    public GeminiContentDto getContent() {
        return content;
    }

    public void setContent(GeminiContentDto content) {
        this.content = content;
    }
}
