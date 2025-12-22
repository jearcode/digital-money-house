package com.dmh.accountservice.dto.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TransferRequestDto {

    @NotBlank(message = "Destination cannot be empty")
    @Size(min = 6, message = "Destination must be at least 6 characters")
    @Pattern(
            regexp = "^(\\d{22}|[a-zA-Z0-9\\.]+)$",
            message = "Destination format invalid. Must be a CVU (22 digits) or an Alias"
    )
    private String destination;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Positive(message = "Amount must be positive")
    private BigDecimal amount;

    @NotBlank(message = "Description cannot be empty")
    @Size(min = 3, max = 50, message = "Description must be between 3 and 50 characters")
    private String description;
}