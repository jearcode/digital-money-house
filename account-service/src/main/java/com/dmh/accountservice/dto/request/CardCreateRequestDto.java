package com.dmh.accountservice.dto.request;

import com.dmh.accountservice.model.enums.CardType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CardCreateRequestDto {

    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "^[0-9]{13,19}$", message = "Card number must be 13-19 digits")
    @Schema(description = "Card number (13-19 digits)", example = "4532015112830366")
    private String number;

    @NotBlank(message = "Cardholder name is required")
    @Size(min = 3, max = 100, message = "Cardholder name must be between 3 and 100 characters")
    @Pattern(regexp = "^[a-zA-ZáéíóúÁÉÍÓÚñÑ\\s]*$", message = "Cardholder name can only contain letters and spaces")
    @Schema(description = "Name of the cardholder", example = "Juan Perez")
    private String cardholderName;

    @NotBlank(message = "Expiration date is required")
    @Pattern(regexp = "^(0[1-9]|1[0-2])/[0-9]{2}$", message = "Expiration date must be in MM/YY format")
    @Schema(description = "Expiration date in MM/YY format", example = "12/25")
    private String expirationDate;

    @NotBlank(message = "CVV is required")
    @Pattern(regexp = "^[0-9]{3,4}$", message = "CVV must be 3 or 4 digits")
    @Schema(description = "Card security code (3-4 digits)", example = "123")
    private String cvv;

    @NotNull(message = "Card type is required")
    @Schema(description = "Type of card (DEBIT or CREDIT)", example = "DEBIT")
    private CardType type;
}