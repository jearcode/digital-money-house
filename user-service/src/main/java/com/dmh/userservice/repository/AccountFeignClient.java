package com.dmh.userservice.repository;

import com.dmh.userservice.dto.AccountDto;
import com.dmh.userservice.dto.AccountRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "account-service")
public interface AccountFeignClient {

    @PostMapping("/accounts")
    AccountDto createAccount(@RequestBody AccountRequestDto accountRequestDto,
                             @RequestHeader("Authorization") String token);

}
