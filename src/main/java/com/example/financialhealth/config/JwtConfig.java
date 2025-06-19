package com.example.financialhealth.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.validation.annotation.Validated;

@Configuration
@ConfigurationProperties(prefix = "jwt")
@Validated
public class JwtConfig {

    @NotBlank(message = "JWT secret key (jwt.secret) must be configured and not blank.")
    // For HS256 (used in JwtUtil), a common recommendation is 256 bits (32 bytes/chars if using ASCII).
    @Size(min = 32, message = "JWT secret key (jwt.secret) must be at least 32 characters long for HS256 algorithm.")
    private String secret;

    // expirationTimeMs is currently hardcoded in JwtUtil.
    // It could also be moved here if desired for external configuration.
    // Example:
    // private long expirationTimeMs = 36000000; // Default 10 hours

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    // Example getter/setter if expirationTimeMs were added:
    // public long getExpirationTimeMs() {
    //     return expirationTimeMs;
    // }
    //
    // public void setExpirationTimeMs(long expirationTimeMs) {
    //     this.expirationTimeMs = expirationTimeMs;
    // }
}
