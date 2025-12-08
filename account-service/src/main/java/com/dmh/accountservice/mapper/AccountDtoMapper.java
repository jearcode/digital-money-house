package com.dmh.accountservice.mapper;

import com.dmh.accountservice.dto.response.AccountDto;
import com.dmh.accountservice.entity.Account;
import org.springframework.stereotype.Component;

@Component
public class AccountDtoMapper {

    public AccountDto toAccountDto(Account account) {
        return AccountDto.builder()
                .id(account.getId())
                .userId(account.getUserId())
                .cvu(account.getCvu())
                .alias(account.getAlias())
                .balance(account.getBalance())
                .build();
    }

    public Account toAccountEntity(AccountDto accountDto) {
        return Account.builder()
                .id(accountDto.getId())
                .userId(accountDto.getUserId())
                .cvu(accountDto.getCvu())
                .alias(accountDto.getAlias())
                .balance(accountDto.getBalance())
                .build();
    }
}