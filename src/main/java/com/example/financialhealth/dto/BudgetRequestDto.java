package com.example.financialhealth.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import java.math.BigDecimal;
import lombok.AllArgsConstructor; // Added
import lombok.Data; // Added
import lombok.NoArgsConstructor; // Added

@Data // Lombok
@NoArgsConstructor // Lombok
@AllArgsConstructor // Lombok
public class BudgetRequestDto {

    @NotNull(message = "Category ID cannot be null.")
    private Long categoryId;

    @NotNull(message = "Allocated amount cannot be null.")
    @DecimalMin(value = "0.00", message = "Allocated amount must be zero or positive.")
    private BigDecimal allocatedAmount;

    @NotBlank(message = "Month cannot be blank.")
    @Pattern(regexp = "^\\d{4}-(0[1-9]|1[0-2])$", message = "Month must be in YYYY-MM format.")
    private String month;

    @DecimalMin(value = "0.00", message = "Total monthly budget goal must be zero or positive.")
    private BigDecimal totalMonthlyBudgetGoal; // Optional

    // Manual getters, setters, and constructors are removed.
}
