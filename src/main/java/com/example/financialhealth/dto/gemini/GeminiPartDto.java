package com.example.financialhealth.dto.gemini;

// Used for both request and response parts if structure is similar
public class GeminiPartDto {
    private String text;

    public GeminiPartDto() {}

    public GeminiPartDto(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
