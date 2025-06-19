package com.example.financialhealth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

// Consider adding validation annotations if spring-boot-starter-validation is present
// import jakarta.validation.constraints.NotBlank;

public class RegisterRequest {

    @NotBlank(message = "Username cannot be blank.")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters.")
    private String username;

    @NotBlank(message = "Password cannot be blank.")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters.")
    // Consider adding @Pattern for complexity if needed, e.g.,
    // @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\S+$).{8,}$", message = "Password must meet complexity requirements.")
    // For now, just length.
    private String password;

    public RegisterRequest() {
    }

    public RegisterRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
