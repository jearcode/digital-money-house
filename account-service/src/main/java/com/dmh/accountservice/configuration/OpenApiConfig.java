package com.dmh.accountservice.configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Digital Money House - Account Service",
                version = "1.0.0",
                description = "Microservice responsible for account management, " +
                        "balance inquires, an automatic CVY/Alias generation"
        )
)
public class OpenApiConfig {
}
