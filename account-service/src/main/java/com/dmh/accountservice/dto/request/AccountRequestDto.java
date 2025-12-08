package com.dmh.accountservice.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

@Data
public class AccountRequestDto {

    @NotNull(message = "User ID cannot be null")
    @Positive(message = "User ID must be a positive number")
    @Schema(description = "Unique ID of the user who owns the account", example = "15",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

}
