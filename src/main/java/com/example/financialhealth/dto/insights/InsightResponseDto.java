package com.example.financialhealth.dto.insights;

public class InsightResponseDto {
    private String adviceText;

    public InsightResponseDto() {
    }

    public InsightResponseDto(String adviceText) {
        this.adviceText = adviceText;
    }

    public String getAdviceText() {
        return adviceText;
    }

    public void setAdviceText(String adviceText) {
        this.adviceText = adviceText;
    }
}
