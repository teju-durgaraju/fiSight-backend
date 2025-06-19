package com.example.financialhealth.config;

/*
 * TODO: Future Enhancement - Implement Refresh Token Mechanism
 * Consider adding a refresh token strategy to allow users to obtain new JWT access tokens
 * without re-authenticating each time the access token expires. This would involve:
 * - Generating and storing refresh tokens securely (e.g., in the database, associated with user).
 * - An endpoint to exchange a valid refresh token for a new access token (and potentially a new refresh token).
 * - Handling refresh token expiration and revocation.
 */

/*
 * TODO: Future Enhancement - Implement Token Revocation Strategy
 * For enhanced security (e.g., on logout, password change, or suspected compromise),
 * consider implementing a token revocation mechanism. Options include:
 * - Maintaining a token blacklist (e.g., in Redis or database). JwtRequestFilter would check against this list.
 * - Using very short-lived access tokens combined with a robust refresh token system.
 * - Implementing a mechanism to track token versions or issued-at timestamps for users.
 */
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import com.example.financialhealth.security.JwtRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;

    public SecurityConfig(JwtRequestFilter jwtRequestFilter) {
        this.jwtRequestFilter = jwtRequestFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/auth/**",                     // Authentication endpoints
                                "/api/hello/public",                // Public test endpoint
                                // Swagger UI / OpenAPI Documentation paths
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                // Note: application.properties uses springdoc.api-docs.path=/api-docs
                                "/api-docs", // Path for OpenAPI spec JSON/YAML as configured
                                "/api-docs/**"  // Subpaths for api-docs, like swagger-config
                        ).permitAll()
                        .anyRequest().authenticated()                 // All other requests need authentication
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class); // Add our JWT filter

        return http.build();
    }
}
