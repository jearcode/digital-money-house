package com.dmh.userservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LogoutRequestDto {

    @NotBlank(message = "Refresh Token is mandatory for logout")
    private String refreshToken;
}
