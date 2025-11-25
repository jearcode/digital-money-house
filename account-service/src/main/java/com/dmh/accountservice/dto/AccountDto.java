package com.dmh.accountservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AccountDto {

    @Schema(description = "Internal Account ID", example = "1")
    private Long id;

    @Schema(description = "User ID owner of the account", example = "15")
    private Long userId;

    @Schema(description = "Unique Virtual Uniform Key (CVU - 22 digits)", example = "0000001234567890123456")
    private String cvu;

    @Schema(description = "Unique Account Alias (3 words)", example = "sun.moon.river")
    private String alias;

    @Schema(description = "Current available balance", example = "0.00")
    private BigDecimal balance;

}
