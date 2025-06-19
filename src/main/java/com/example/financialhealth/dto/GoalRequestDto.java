package com.example.financialhealth.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.AllArgsConstructor; // Added
import lombok.Data; // Added
import lombok.NoArgsConstructor; // Added

@Data // Lombok
@NoArgsConstructor // Lombok
@AllArgsConstructor // Lombok
public class GoalRequestDto {

    @NotBlank(message = "Goal name cannot be blank.")
    @Size(max = 255, message = "Goal name cannot exceed 255 characters.")
    private String goalName;

    @NotNull(message = "Target amount cannot be null.")
    @DecimalMin(value = "0.01", message = "Target amount must be positive and greater than 0.")
    private BigDecimal targetAmount;

    @DecimalMin(value = "0.00", message = "Current amount must be zero or positive.")
    private BigDecimal currentAmount; // Optional

    @FutureOrPresent(message = "Target date must be in the present or future.")
    private LocalDate targetDate; // Optional

    // Manual getters, setters, and constructors are removed.
}
