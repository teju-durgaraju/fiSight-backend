package com.example.financialhealth.dto.gemini;

import java.util.List;

public class GeminiRequestDto {
    private List<GeminiContentDto> contents;

    public GeminiRequestDto() {}

    public GeminiRequestDto(List<GeminiContentDto> contents) {
        this.contents = contents;
    }

    public List<GeminiContentDto> getContents() {
        return contents;
    }

    public void setContents(List<GeminiContentDto> contents) {
        this.contents = contents;
    }
}
