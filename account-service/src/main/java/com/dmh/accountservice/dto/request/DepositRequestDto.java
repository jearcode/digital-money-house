package com.dmh.accountservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Data
public class DepositRequestDto {

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.00", message = "Amount must be greater than 1")
    @Positive(message = "Amount must be a positive value")
    @Schema(description = "Amount to deposit", example = "100.50")
    private BigDecimal amount;

    @NotNull(message = "Card ID is required")
    @Positive(message = "Card ID must be a positive number")
    @Schema(description = "ID of the card to deposit to", example = "12345")
    private Long cardId;


}