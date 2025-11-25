package com.dmh.accountservice.controller;

import com.dmh.accountservice.dto.AccountDto;
import com.dmh.accountservice.dto.AccountRequestDto;
import com.dmh.accountservice.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
@Tag(name = "Accounts", description = "Endpoints for managing bank accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController (AccountService accountService) {
        this.accountService = accountService;
    }

    @Operation(
            summary = "Create a new account",
            description = "Automatically generates a unique CVU and Alias for a registered user. Restricted to internal use (requires SERVICE role)."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Account successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input data (e.g., null or negative ID)"),
            @ApiResponse(responseCode = "409", description = "User already has a registered account"),
            @ApiResponse(responseCode = "403", description = "Forbidden (Requires valid Service Token)")
    })
    @PostMapping
    public ResponseEntity<AccountDto> create(@RequestBody AccountRequestDto request) {

        AccountDto newAccount = accountService.createAccount(request.getUserId());

        return ResponseEntity.status(HttpStatus.CREATED).body(newAccount);

    }

}
