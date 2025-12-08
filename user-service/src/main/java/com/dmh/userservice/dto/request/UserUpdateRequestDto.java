package com.dmh.userservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.lang.Nullable;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class UserUpdateRequestDto {

    @Nullable
    @Size(min = 2, max = 100, message = "First name must be between 2 and 100 characters")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*$", message = "First name can only contain letters and spaces")
    private String firstName;

    @Nullable
    @Size(min = 2, max = 100, message = "Last name must be between 2 and 100 characters")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*$", message = "Last name can only contain letters and spaces")
    private String lastName;

    @Nullable
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "Email cannot exceed 255 characters")
    private String email;

    @Nullable
    @Size(min = 8, max = 20, message = "DNI must be between 8 and 20 characters")
    @Pattern(regexp = "^[0-9a-zA-Z]*$", message = "DNI can only contain numbers and letters")
    private String dni;

    @Nullable
    @Size(min = 7, max = 20, message = "Phone must be between 7 and 20 characters")
    @Pattern(regexp = "^[+]?[0-9\\s\\-()]*$", message = "Phone contains invalid characters")
    private String phone;

    @Nullable
    @Size(min = 6, max = 255, message = "Password must be between 6 and 255 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[a-zA-Z\\d@$!%*?&]*$",
            message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character (@$!%*?&)")
    private String password;

}