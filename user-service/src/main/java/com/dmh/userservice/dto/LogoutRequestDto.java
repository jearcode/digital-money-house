package com.dmh.userservice.dto;

import lombok.Data;

@Data
public class LogoutRequestDto {
    private String refreshToken;
}
