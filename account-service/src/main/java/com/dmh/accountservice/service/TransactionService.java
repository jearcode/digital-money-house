package com.dmh.accountservice.service;

import com.dmh.accountservice.dto.response.TransactionResponseDto;
import com.dmh.accountservice.entity.Transaction;
import com.dmh.accountservice.exception.AccountNotFoundForTransactionException;
import com.dmh.accountservice.exception.NoTransactionFoundException;
import com.dmh.accountservice.exception.NoTransactionsFoundException;
import com.dmh.accountservice.mapper.TransactionDtoMapper;
import com.dmh.accountservice.repository.AccountRepository;
import com.dmh.accountservice.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final TransactionDtoMapper transactionDtoMapper;

    public TransactionService(
            TransactionRepository transactionRepository,
            AccountRepository accountRepository, TransactionDtoMapper transactionDtoMapper) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.transactionDtoMapper = transactionDtoMapper;
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
}