package com.example.financialhealth.dto;

// Consider adding validation annotations if spring-boot-starter-validation is present
// import jakarta.validation.constraints.NotBlank;

public class RegisterRequest {

    // @NotBlank(message = "Username cannot be blank")
    private String username;

    // @NotBlank(message = "Password cannot be blank")
    // @Size(min = 8, message = "Password must be at least 8 characters long") // Example
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
