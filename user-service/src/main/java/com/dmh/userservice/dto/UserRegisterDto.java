package com.dmh.userservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class UserRegisterDto {

    @Schema(example = "John", description = "User's first name")
    private String firstName;

    @Schema(example = "Doe", description = "User's last name")
    private String lastName;

    @Schema(example = "12345678", description = "National Identity Document (DNI)")
    private String dni;

    @Schema(example = "john.doe@example.com", description = "User's email address (used as username)")
    private String email;

    @Schema(example = "securePassword123!", description = "User's password")
    private String password;

    @Schema(example = "+5491112345678", description = "User's phone number")
    private String phone;

}
