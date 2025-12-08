package com.dmh.accountservice.controller;

import com.dmh.accountservice.dto.request.AccountRequestDto;
import com.dmh.accountservice.dto.request.CardCreateRequestDto;
import com.dmh.accountservice.dto.response.AccountDto;
import com.dmh.accountservice.dto.response.CardResponseDto;
import com.dmh.accountservice.entity.Transaction;
import com.dmh.accountservice.service.AccountService;
import com.dmh.accountservice.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/accounts")
@EnableMethodSecurity
@Tag(name = "Accounts", description = "Endpoints for managing bank accounts and cards")
public class AccountController {

    private final AccountService accountService;
    private final TransactionService transactionService;

    public AccountController(
            AccountService accountService,
            TransactionService transactionService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    // ============ ACCOUNT ENDPOINTS ============

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
    @PreAuthorize("hasRole('SERVICE')")
    public ResponseEntity<AccountDto> createAccount(@Valid @RequestBody AccountRequestDto request) {
        AccountDto newAccount = accountService.createAccount(request.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(newAccount);
    }

    @Operation(
            summary = "Get account details by account ID",
            description = "Returns account information (CVU, alias, balance). Accessible by account owner or SERVICE role."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission to access this account")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('SERVICE') or @accountSecurity.isOwner(authentication, #id)")
    public ResponseEntity<AccountDto> getAccountById(@PathVariable Long id) {
        AccountDto accountDto = accountService.findAccountById(id);
        return ResponseEntity.ok(accountDto);
    }

    @Operation(
            summary = "Get account by user ID",
            description = "Retrieves the account associated with a specific user. Accessible by admin or the user owner."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Account retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found for this user"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission")
    })
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('SERVICE') or @accountSecurity.isOwner(authentication, #userId)")
    public ResponseEntity<AccountDto> getAccountByUserId(@PathVariable Long userId) {
        AccountDto accountDto = accountService.findAccountByUserId(userId);
        return ResponseEntity.ok(accountDto);
    }

    // ============ TRANSACTION ENDPOINTS ============

    @Operation(
            summary = "Get all transactions for an account",
            description = "Returns a list of transactions ordered by most recent first. Accessible by admin or account owner."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transactions retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission")
    })
    @GetMapping("/{id}/transactions")
    @PreAuthorize("hasRole('SERVICE') or @accountSecurity.isOwner(authentication, #id)")
    public ResponseEntity<List<Transaction>> getAccountTransactions(@PathVariable Long id) {
        List<Transaction> transactions = transactionService.findAllTransactionsByAccountId(id);
        return ResponseEntity.ok(transactions);
    }

    // ============ CARD ENDPOINTS ============

    @Operation(
            summary = "Add a card to an account",
            description = "Associates a new card (credit or debit) with an account. Card number must be unique across all accounts."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Card added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid card data (bad format, expired, etc)"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "409", description = "Card already registered to another account"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission")
    })
    @PostMapping("/{id}/cards")
    @PreAuthorize("hasRole('SERVICE') or @accountSecurity.isOwner(authentication, #id)")
    public ResponseEntity<CardResponseDto> addCard(
            @PathVariable Long id,
            @Valid @RequestBody CardCreateRequestDto cardRequest) throws Exception {

        CardResponseDto card = accountService.addCardToAccount(id, cardRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(card);
    }

    @Operation(
            summary = "Get all cards for an account",
            description = "Returns a list of all cards associated with the account. Card numbers are masked for security."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cards retrieved successfully (may be empty list)"),
            @ApiResponse(responseCode = "404", description = "Account not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission")
    })
    @GetMapping("/{id}/cards")
    @PreAuthorize("hasRole('SERVICE') or @accountSecurity.isOwner(authentication, #id)")
    public ResponseEntity<List<CardResponseDto>> getAccountCards(@PathVariable Long id) {
        List<CardResponseDto> cards = accountService.getAccountCards(id);
        return ResponseEntity.ok(cards);
    }

    @Operation(
            summary = "Get a specific card details",
            description = "Retrieves details of a specific card. Card number is masked. Accessible by admin or account owner."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Account or card not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission")
    })
    @GetMapping("/{id}/cards/{cardId}")
    @PreAuthorize("hasRole('SERVICE') or @accountSecurity.isOwner(authentication, #id)")
    public ResponseEntity<CardResponseDto> getCard(
            @PathVariable Long id,
            @PathVariable Long cardId) {

        CardResponseDto card = accountService.getAccountCard(id, cardId);
        return ResponseEntity.ok(card);
    }

    @Operation(
            summary = "Delete a card",
            description = "Removes a card from an account. The card can no longer be used for transactions."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Card deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Account or card not found"),
            @ApiResponse(responseCode = "403", description = "Forbidden - You don't have permission")
    })
    @DeleteMapping("/{id}/cards/{cardId}")
    @PreAuthorize("hasRole('SERVICE') or @accountSecurity.isOwner(authentication, #id)")
    public ResponseEntity<Void> deleteCard(
            @PathVariable Long id,
            @PathVariable Long cardId) {

        accountService.deleteAccountCard(id, cardId);
        return ResponseEntity.noContent().build();
    }
}