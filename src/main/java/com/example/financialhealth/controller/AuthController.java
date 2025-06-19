package com.example.financialhealth.controller;

import com.example.financialhealth.dto.LoginRequest;
import com.example.financialhealth.dto.LoginResponse;
import com.example.financialhealth.dto.RegisterRequest;
import com.example.financialhealth.model.Role;
import com.example.financialhealth.model.User;
import com.example.financialhealth.repository.RoleRepository;
import com.example.financialhealth.repository.UserRepository;
import com.example.financialhealth.security.JwtUtil;
import com.example.financialhealth.service.CustomUserDetailsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid; // Added
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@Tag(name = "Authentication", description = "Endpoints for user registration and login.") // Added
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService userDetailsService; // Can be used to load UserDetails

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          UserRepository userRepository,
                          RoleRepository roleRepository,
                          PasswordEncoder passwordEncoder,
                          CustomUserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }

    @Operation(summary = "Register a new user", description = "Creates a new user account.")
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegisterRequest registerRequest) { // Added @Valid
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            return ResponseEntity.badRequest().body("Error: Username is already taken!");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Error: Role ROLE_USER is not found. Please ensure it's initialized in the database."));
        user.setRoles(Collections.singleton(userRole));

        userRepository.save(user);

        return ResponseEntity.ok("User registered successfully!");
    }

    @Operation(summary = "Authenticate a user", description = "Authenticates a user with username and password, and returns a JWT upon success.")
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) { // Added @Valid
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            // UserDetails userDetails = userDetailsService.loadUserByUsername(loginRequest.getUsername()); // Alternative
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();


            String jwt = jwtUtil.generateToken(userDetails);
            return ResponseEntity.ok(new LoginResponse(jwt));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Error: Invalid username or password");
        } catch (Exception e) {
            // Log the exception server-side for debugging
            // logger.error("Authentication error for user {}: {}", loginRequest.getUsername(), e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during authentication: " + e.getMessage());
        }
    }
}
