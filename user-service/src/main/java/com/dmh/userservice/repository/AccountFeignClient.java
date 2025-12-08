package com.dmh.userservice.repository;

import com.dmh.userservice.dto.response.AccountResponseDto;
import com.dmh.userservice.dto.request.AccountRequestDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "account-service")
public interface AccountFeignClient {

    @PostMapping("/accounts")
    AccountResponseDto createAccount(@RequestBody AccountRequestDto accountRequestDto,
                                     @RequestHeader("Authorization") String token);

    @GetMapping("/accounts/user/{userId}")
    AccountResponseDto findAccountByUserId(@PathVariable Long userId,
                                          @RequestHeader("Authorization") String token);

}
