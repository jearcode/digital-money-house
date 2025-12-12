package com.dmh.accountservice.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.dmh.accountservice.model.enums.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TransactionResponseDto {
    private Long id;
    private Long accountId;
    private BigDecimal amount;
    private TransactionType type;
    private String description;
    private String origin;
    private String destinationAccount;
    private LocalDateTime date;
}