package com.dmh.accountservice.mapper;

import com.dmh.accountservice.dto.response.TransactionResponseDto;
import com.dmh.accountservice.entity.Transaction;
import org.springframework.stereotype.Component;

@Component
public class TransactionDtoMapper {

    public TransactionResponseDto toDto(Transaction transaction) {

        return TransactionResponseDto.builder()
                .id(transaction.getId())
                .accountId(transaction.getAccountId())
                .amount(transaction.getAmount())
                .type(transaction.getTransactionType())
                .description(transaction.getDescription())
                .origin(transaction.getOrigin())
                .destinationAccount(transaction.getDestinationAccount())
                .date(transaction.getTransactionDate())
                .build();
    }

}