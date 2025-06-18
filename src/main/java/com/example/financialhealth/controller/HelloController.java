package com.example.financialhealth.controller;

import com.example.financialhealth.model.User;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/hello")
public class HelloController {

    @GetMapping("/public")
    public String helloPublic() {
        return "Hello Public!";
    }

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public String helloUser() {
        return "Hello User! Your ID is: " + getAuthenticatedUserId() + " and username: " + getAuthenticatedUsername();
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public String helloAdmin() {
        return "Hello Admin! Your ID is: " + getAuthenticatedUserId() + " and username: " + getAuthenticatedUsername();
    }

    @GetMapping("/any")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public String helloAnyAuthenticated() {
        return "Hello Authenticated User (User or Admin)! Your ID is: " + getAuthenticatedUserId() + " and username: " + getAuthenticatedUsername();
    }

    private String getAuthenticatedUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && !(authentication instanceof AnonymousAuthenticationToken)) {
            return authentication.getName();
        }
        return "N/A";
    }

    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() && authentication.getPrincipal() instanceof User) {
            User user = (User) authentication.getPrincipal();
            return user.getId();
        }
        // For AnonymousAuthenticationToken, getPrincipal() might be a String "anonymousUser"
        // or an AnonymousAuthenticationToken object itself depending on the Spring Security version and config.
        // Explicitly checking for our User type is robust.
        return null;
    }
}
