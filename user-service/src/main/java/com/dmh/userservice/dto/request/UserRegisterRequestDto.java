package com.dmh.userservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserRegisterRequestDto {

    @NotBlank(message = "First name cannot be empty")
    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*$", message = "First name can only contain letters and spaces")
    @Schema(example = "John", description = "User's first name")
    private String firstName;

    @NotBlank(message = "Last name cannot be empty")
    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*$", message = "Last name can only contain letters and spaces")
    @Schema(example = "Doe", description = "User's last name")
    private String lastName;

    @NotBlank(message = "DNI cannot be empty")
    @Size(min = 8, max = 20, message = "DNI must be between 8 and 20 characters")
    @Pattern(regexp = "^[0-9a-zA-Z]*$", message = "DNI can only contain numbers and letters")
    @Schema(example = "12345678", description = "National Identity Document (DNI)")
    private String dni;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    @Schema(example = "john.doe@example.com", description = "User's email address (used as username)")
    private String email;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, max = 255, message = "Password must be between 6 and 255 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[a-zA-Z\\d@$!%*?&]*$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one number, and one special character (@$!%*?&)")
    @Schema(example = "securePassword123!", description = "User's password")
    private String password;

    @NotBlank(message = "Phone cannot be empty")
    @Size(min = 7, max = 20, message = "Phone must be between 7 and 20 characters")
    @Pattern(regexp = "^[+]?[0-9\\s\\-()]*$", message = "Phone contains invalid characters")
    @Schema(example = "+5491112345678", description = "User's phone number")
    private String phone;

}