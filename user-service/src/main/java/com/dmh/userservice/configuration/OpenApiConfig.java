package com.dmh.userservice.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Digital Money House - User Service",
                version = "1.0.0",
                description = "Microservice for user management, registration orchestration, and profile data persistence."
        )
)
public class OpenApiConfig {
}