package com.example.financialhealth.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor; // Added
import lombok.Data; // Added
import lombok.NoArgsConstructor; // Added

// For validation annotations (e.g., @NotNull, @NotBlank, @Size, @Positive, @PastOrPresent) to take effect,
// ensure 'spring-boot-starter-validation' is included in your pom.xml:
// <dependency>
//     <groupId>org.springframework.boot</groupId>
//     <artifactId>spring-boot-starter-validation</artifactId>
// </dependency>
// (This dependency has now been added to pom.xml)

@Data // Lombok
@NoArgsConstructor // Lombok
@AllArgsConstructor // Lombok
public class TransactionRequestDto {

    @NotBlank(message = "Transaction type cannot be blank. Must be INCOME or EXPENSE.")
    private String type; // Will be mapped to TransactionType enum in service

    @NotNull(message = "Amount cannot be null.")
    @DecimalMin(value = "0.01", message = "Amount must be positive and greater than 0.")
    private BigDecimal amount;

    @NotNull(message = "Category ID cannot be null.")
    @Schema(description = "ID of the category for this transaction.", example = "1") // Added
    private Long categoryId;

    @NotNull(message = "Transaction date cannot be null.")
    @PastOrPresent(message = "Transaction date cannot be in the future.")
    private LocalDate transactionDate;

    @Size(max = 255, message = "Description cannot exceed 255 characters.")
    private String description;

    // Manual getters, setters, and constructors are removed.
}
