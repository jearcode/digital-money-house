package com.dmh.accountservice.dto.response;

import com.dmh.accountservice.model.enums.CardType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CardResponseDto {

    @Schema(description = "Card ID", example = "1")
    private Long id;

    @Schema(description = "Account ID that owns the card", example = "1")
    private Long accountId;

    @Schema(description = "Last 4 digits of the card", example = "0366")
    private String lastFourDigits;

    @Schema(description = "Cardholder name", example = "Juan Perez")
    private String cardholderName;

    @Schema(description = "Expiration date in MM/YY format", example = "12/25")
    private String expirationDate;

    @Schema(description = "Card type (DEBIT or CREDIT)", example = "DEBIT")
    private CardType type;

    @Schema(description = "Whether the card is active", example = "true")
    private Boolean isActive;

    @Schema(description = "When the card was created", example = "2025-12-05T10:30:00")
    private LocalDateTime createdAt;
}
