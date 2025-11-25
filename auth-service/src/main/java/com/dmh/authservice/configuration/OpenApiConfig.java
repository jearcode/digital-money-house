package com.dmh.authservice.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Digital Money House - Auth Service",
                version = "1.0.0",
                description = "Authentication Microservice acting as a proxy for Keycloak (Login/Logout)."
        )
)
public class OpenApiConfig {
}