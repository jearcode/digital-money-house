package com.dmh.authservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class LoginRequestDto {

    @Schema(example = "pepito@test.com", description = "Registered email")
    private String email;

    @Schema(example = "123", description = "User password")
    private String password;

}
