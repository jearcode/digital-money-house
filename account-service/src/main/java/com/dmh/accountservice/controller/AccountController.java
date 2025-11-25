package com.dmh.accountservice.controller;

import com.dmh.accountservice.dto.AccountDto;
import com.dmh.accountservice.dto.AccountRequestDto;
import com.dmh.accountservice.service.AccountService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController (AccountService accountService) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountDto> create(@RequestBody AccountRequestDto request) {

        AccountDto newAccount = accountService.createAccount(request.getUserId());

        return ResponseEntity.status(HttpStatus.CREATED).body(newAccount);

    }

}
