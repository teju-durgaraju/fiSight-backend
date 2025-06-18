package com.example.financialhealth.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
// import io.swagger.v3.oas.models.info.Info; // Not needed if relying on properties
// import io.swagger.v3.oas.models.info.License;
// import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    // The Info details (title, version, description) are configured via application.properties
    // (e.g., springdoc.api-docs.info.title) and will be automatically picked up by Springdoc.
    // This bean focuses on adding the JWT Bearer Authentication security scheme.

    @Bean
    public OpenAPI customOpenAPI() {
        final String securitySchemeName = "bearerAuth"; // Can be any name, used as a reference

        // Define the security scheme for JWT Bearer Authentication
        SecurityScheme securityScheme = new SecurityScheme()
            .name(securitySchemeName) // This name is used as the key in components.securitySchemes
            .type(SecurityScheme.Type.HTTP) // Identifies the scheme as HTTP-based
            .scheme("bearer") // The scheme name to be used in the Authorization header (e.g., Bearer)
            .bearerFormat("JWT") // Specifies the format of the bearer token, "JWT" is common
            .description("JWT Bearer token for authentication. Example: Bearer {token}"); // Optional description

        // Define the security requirement
        // This makes the "bearerAuth" scheme a global requirement for all API operations.
        // For public endpoints (like /api/auth/login), they will still appear secured in Swagger UI,
        // but Spring Security configuration will allow access without a token.
        // More fine-grained control can be achieved using @SecurityRequirement at method/class level.
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(securitySchemeName);

        return new OpenAPI()
            // The .info() part is automatically populated from application.properties by Springdoc.
            // If you were to set it here, e.g., .info(new Info().title("API via Bean")),
            // it could override or merge with properties based on Springdoc's behavior.
            // For this setup, relying on properties for Info is cleaner.
            .addSecurityItem(securityRequirement) // Add the global security requirement
            .components(new Components()
                .addSecuritySchemes(securitySchemeName, securityScheme)); // Add the defined security scheme to components
    }
}
