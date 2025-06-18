package com.example.financialhealth.dto.insights;

import jakarta.validation.constraints.NotBlank;

public class InsightRequestDto {

    @NotBlank(message = "User query cannot be blank.")
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
