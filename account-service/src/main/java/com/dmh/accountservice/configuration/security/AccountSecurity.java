package com.dmh.accountservice.configuration.security;

import com.dmh.accountservice.dto.response.UserDto;
import com.dmh.accountservice.entity.Account;
import com.dmh.accountservice.repository.AccountRepository;
import com.dmh.accountservice.repository.UserFeignClient;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Component;

@Component("accountSecurity")
public class AccountSecurity {

    private final AccountRepository accountRepository;
    private final UserFeignClient userFeignClient;

    public AccountSecurity (AccountRepository accountRepository, UserFeignClient userFeignClient) {
        this.accountRepository = accountRepository;
        this.userFeignClient = userFeignClient;
    }

    public boolean isOwner (Authentication authentication, Long accountId) {
        try {
            Jwt jwt = (Jwt) authentication.getPrincipal();

            Account account = accountRepository.findById(accountId).orElse(null);
            if (account == null) return false;

            String tokenHeader = "Bearer " + jwt.getTokenValue();
            UserDto userDto = userFeignClient.getMyProfile(tokenHeader);

            return account.getUserId().equals(userDto.getId());

        } catch (Exception e) {
            return false;
        }
    }

}
