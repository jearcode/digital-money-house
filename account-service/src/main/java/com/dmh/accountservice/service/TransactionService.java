package com.dmh.accountservice.service;

import com.dmh.accountservice.configuration.security.AccountSecurity;
import com.dmh.accountservice.dto.response.RecipientDto;
import com.dmh.accountservice.dto.response.TransactionResponseDto;
import com.dmh.accountservice.dto.response.UserDto;
import com.dmh.accountservice.entity.Account;
import com.dmh.accountservice.entity.Transaction;
import com.dmh.accountservice.exception.AccountNotFoundForTransactionException;
import com.dmh.accountservice.exception.NoTransactionFoundException;
import com.dmh.accountservice.exception.NoTransactionsFoundException;
import com.dmh.accountservice.mapper.TransactionDtoMapper;
import com.dmh.accountservice.model.enums.TransactionType;
import com.dmh.accountservice.repository.AccountRepository;
import com.dmh.accountservice.repository.TransactionRepository;
import com.dmh.accountservice.repository.UserFeignClient;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionDtoMapper transactionDtoMapper;
    private final AccountService accountService;
    private final UserFeignClient userFeignClient;
    private final Keycloak keycloak;

    public TransactionService(
            TransactionRepository transactionRepository,
            AccountRepository accountRepository, TransactionDtoMapper transactionDtoMapper, AccountService accountService, AccountSecurity accountSecurity, UserFeignClient userFeignClient, Keycloak keycloak) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.transactionDtoMapper = transactionDtoMapper;
        this.accountService = accountService;
        this.userFeignClient = userFeignClient;
        this.keycloak = keycloak;
    }

    public List<TransactionResponseDto> findAllTransactionsByAccountId(Long accountId) {

        validateAccountExists(accountId);

        List<Transaction> transactionEntities = transactionRepository
                .findAllByAccountIdOrderByTransactionDateDesc(accountId);

        validateTransactionsExist(accountId, transactionEntities);

        return transactionEntities.stream()
                .map(transactionDtoMapper::toDto)
                .collect(Collectors.toList());
    }

    private void validateAccountExists(Long accountId) {
        boolean accountExists = accountRepository.existsById(accountId);

        if (!accountExists) {
            throw new AccountNotFoundForTransactionException(accountId);
        }
    }

    private Transaction validateTransactionExists (Long accountId, Long transactionId) {

        Transaction transaction = transactionRepository.findByAccountIdAndId(accountId, transactionId).orElseThrow(
                () -> new NoTransactionFoundException(transactionId)
        );

        return transaction;
    }


    private void validateTransactionsExist(Long accountId, List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            throw new NoTransactionsFoundException(accountId);
        }
    }

    public TransactionResponseDto findTransactionByIdAndAccountId(Long transactionId, Long id) {

        validateAccountExists(id);

        return transactionDtoMapper.toDto(validateTransactionExists(id, transactionId));

    }

    public List<RecipientDto> getLastRecipients (Long accountId) {

        accountService.findAccountEntityById(accountId);

        List<Transaction> recentTransfers = transactionRepository
                .findAllByAccountIdAndTransactionTypeOrderByTransactionDateDesc(accountId, TransactionType.TRANSFER_SENT);

        List<String> uniqueCvusc = recentTransfers.stream()
                .map(Transaction::getDestinationAccount)
                .distinct()
                .limit(10)
                .toList();

        List<RecipientDto> recipients = uniqueCvusc.stream()
                .map((cvu) -> mapCvuToRecipientDto(cvu))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        return recipients;
    }

    private RecipientDto mapCvuToRecipientDto(String cvu) {

        Account account = accountService.findAccountByCvu(cvu);

        try {
            String serviceToken = keycloak.tokenManager().getAccessToken().getToken();
            UserDto user = userFeignClient.getUserById("Bearer " + serviceToken, account.getUserId());
            return RecipientDto.builder()
                    .fullName(user.getFirstName() + " " + user.getLastName())
                    .cvu(cvu)
                    .alias(account.getAlias())
                    .build();
        } catch (Exception e) {
            log.warn("Information could not be obtained for CVU: {}", cvu, e);
            return null;
        }

    }


}