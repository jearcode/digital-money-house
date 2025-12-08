package com.dmh.userservice.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AccountResponseDto {

    private Long id;
    private Long userId;
    private String cvu;
    private String alias;
    private BigDecimal balance;

}