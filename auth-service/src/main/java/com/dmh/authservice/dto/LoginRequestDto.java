package com.dmh.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDto {

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    @Schema(example = "pepito@test.com", description = "Registered email")
    private String email;

    @NotBlank(message = "Password cannot be empty")
    @Schema(example = "123", description = "User password")
    private String password;

}
