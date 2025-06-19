package com.example.financialhealth.dto;

import jakarta.validation.constraints.NotBlank;

// Consider adding validation annotations if spring-boot-starter-validation is present
// import jakarta.validation.constraints.NotBlank;

public class LoginRequest {

    @NotBlank(message = "Username cannot be blank.")
    private String username;

    @NotBlank(message = "Password cannot be blank.")
    private String password;

    public LoginRequest() {
    }

    public LoginRequest(String username, String password) {
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
