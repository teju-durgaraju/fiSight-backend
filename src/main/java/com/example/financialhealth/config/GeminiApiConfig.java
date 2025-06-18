package com.example.financialhealth.config;

import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "gemini.api")
@Validated // Enable validation of configuration properties
public class GeminiApiConfig {

    @NotBlank(message = "Gemini API key must be configured in application.properties or environment variables (gemini.api.key)")
    private String key;

    @NotBlank(message = "Gemini API URL must be configured (gemini.api.url)")
    private String url;

    // Standard getters and setters
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
