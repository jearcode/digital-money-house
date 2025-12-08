package com.dmh.userservice.provider.account;

import com.dmh.userservice.dto.response.AccountResponseDto;

public interface AccountProvider {
    AccountResponseDto createAccount(Long userId);
    AccountResponseDto findAccountByUserId(Long userId);
}
