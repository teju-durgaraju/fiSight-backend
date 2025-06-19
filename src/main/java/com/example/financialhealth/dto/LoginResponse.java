package com.example.financialhealth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String jwtToken;

    // Manual constructor, getter, and potential setter are removed.
    // @AllArgsConstructor covers the constructor.
    // @Data provides getter and setter.
}
