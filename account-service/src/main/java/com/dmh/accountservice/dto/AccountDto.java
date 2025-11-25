package com.dmh.accountservice.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class AccountDto {

    private Long id;
    private Long userId;
    private String cvu;
    private String alias;
    private BigDecimal balance;

}
