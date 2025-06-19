package com.example.financialhealth.dto.gemini;

import java.util.List;
import lombok.AllArgsConstructor; // Added
import lombok.Data; // Added
import lombok.NoArgsConstructor; // Added

@Data // Lombok
@NoArgsConstructor // Lombok
@AllArgsConstructor // Lombok
// For request, role might be "user". For response, role might be "model".
// Add 'role' if needed based on exact API interaction. For simplicity, starting without.
public class GeminiContentDto {
    private List<GeminiPartDto> parts;
    // Optional: private String role;

    // Manual getters, setters, and constructors are removed.

    // public String getRole() { return role; }
    // public void setRole(String role) { this.role = role; }
}
