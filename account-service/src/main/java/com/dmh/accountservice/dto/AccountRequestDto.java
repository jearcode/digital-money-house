package com.dmh.accountservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AccountRequestDto {

    @Schema(description = "Unique ID of the user who owns the account", example = "15",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Long userId;

}
