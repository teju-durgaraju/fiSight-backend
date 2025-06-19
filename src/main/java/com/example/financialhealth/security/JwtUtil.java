package com.example.financialhealth.security;

import com.example.financialhealth.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import com.example.financialhealth.config.JwtConfig; // Added
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
// Removed: import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    // Removed: private final String secretKeyString;
    private final Key signingKey;

    // EXPIRATION_TIME_MS can remain a static final long or be configurable too
    private static final long EXPIRATION_TIME_MS = 1000 * 60 * 60 * 10; // 10 hours

    public JwtUtil(JwtConfig jwtConfig) { // Changed parameter from @Value String to JwtConfig
        String secretKeyString = jwtConfig.getSecret();
        if ("DefaultSecretKeyPlaceholder_ChangeThisImmediately_0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".equals(secretKeyString)) {
            // Log a strong warning or consider throwing an exception in a production profile
            // For now, a system error print for high visibility during development.
            logger.error("CRITICAL WARNING: JWT Secret Key is using the default placeholder value. This is highly insecure and MUST be changed for any real deployment.");
            // Optionally, in a 'prod' profile, you might throw new IllegalStateException("Cannot start with default JWT secret.");
        }
        // this.secretKeyString = secretKeyString; // No longer need to store it as a field
        byte[] keyBytes = secretKeyString.getBytes(StandardCharsets.UTF_8);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        if (userDetails instanceof User) {
            User customUser = (User) userDetails;
            claims.put("userId", customUser.getId());
        }
        claims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME_MS))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Boolean isTokenExpired(String token) {
        try {
            return extractClaim(token, Claims::getExpiration).before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            // Consider logging this exception
            return true; // If any error in parsing expiration, treat as expired/invalid
        }
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (JwtException | IllegalArgumentException e) {
            // Consider logging this exception
            return false;
        }
    }

    private Key getSigningKey() {
        // Now returns the pre-initialized signingKey field
        return signingKey;
    }

    public List<String> extractRoles(String token) {
        return extractClaim(token, claims -> claims.get("roles", List.class));
    }
}
