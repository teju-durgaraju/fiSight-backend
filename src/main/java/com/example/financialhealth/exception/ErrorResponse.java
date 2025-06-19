package com.example.financialhealth.exception;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor; // Added
import lombok.Data; // Added
import lombok.NoArgsConstructor; // Added

@Data // Lombok
@NoArgsConstructor // Lombok
@AllArgsConstructor // Lombok
public class ErrorResponse {
    private LocalDateTime timestamp; // Removed final
    private int status; // Removed final
    private String error; // Removed final
    private String message; // Removed final
    private String path; // Removed final

    // Manual constructor and getters are removed.
}
