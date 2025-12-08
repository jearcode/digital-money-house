package com.dmh.userservice.provider.account;

import com.dmh.userservice.dto.response.AccountResponseDto;
import com.dmh.userservice.dto.request.AccountRequestDto;
import com.dmh.userservice.exception.AccountServiceException;
import com.dmh.userservice.repository.AccountFeignClient;
import org.keycloak.admin.client.Keycloak;
import org.springframework.stereotype.Component;

@Component
public class AccountFeignClientProvider implements AccountProvider{

    private final AccountFeignClient accountFeignClient;
    private final Keycloak keycloak;

    public AccountFeignClientProvider(AccountFeignClient accountFeignClient, Keycloak keycloak) {
        this.accountFeignClient = accountFeignClient;
        this.keycloak = keycloak;
    }

    @Override
    public AccountResponseDto createAccount(Long userId) {
        try {
            String serviceToken = keycloak.tokenManager().getAccessToken().getToken();
            AccountRequestDto accountRequestDto = new AccountRequestDto(userId);
            return accountFeignClient.createAccount(accountRequestDto, "Bearer " + serviceToken);
        } catch (Exception e) {
            throw new AccountServiceException("Error creating account for user " + userId, e);
        }
    }

    @Override
    public AccountResponseDto findAccountByUserId(Long userId) {
        try {
            String serviceToken = keycloak.tokenManager().getAccessToken().getToken();
            return accountFeignClient.findAccountByUserId(userId, "Bearer " + serviceToken);
        } catch (Exception e) {
            throw new AccountServiceException("Failed to retrieve account for user " + userId, e);
        }
    }
}
