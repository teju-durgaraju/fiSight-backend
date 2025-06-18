package com.example.financialhealth.dto.gemini;

import java.util.List;

// For request, role might be "user". For response, role might be "model".
// Add 'role' if needed based on exact API interaction. For simplicity, starting without.
public class GeminiContentDto {
    private List<GeminiPartDto> parts;
    // Optional: private String role;

    public GeminiContentDto() {}

    public GeminiContentDto(List<GeminiPartDto> parts) {
        this.parts = parts;
    }

    public List<GeminiPartDto> getParts() {
        return parts;
    }

    public void setParts(List<GeminiPartDto> parts) {
        this.parts = parts;
    }

    // public String getRole() { return role; }
    // public void setRole(String role) { this.role = role; }
}
