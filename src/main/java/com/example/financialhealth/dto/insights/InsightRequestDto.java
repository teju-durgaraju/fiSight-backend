package com.example.financialhealth.dto.insights;

import io.swagger.v3.oas.annotations.media.Schema; // Added
import jakarta.validation.constraints.NotBlank;

public class InsightRequestDto {

    @NotBlank(message = "User query cannot be blank.")
    @Schema(description = "User's question or topic for financial advice.", example = "How can I save more money?") // Added
    private String userQuery;

    public InsightRequestDto() {
    }

    public InsightRequestDto(String userQuery) {
        this.userQuery = userQuery;
    }

    public String getUserQuery() {
        return userQuery;
    }

    public void setUserQuery(String userQuery) {
        this.userQuery = userQuery;
    }
}
