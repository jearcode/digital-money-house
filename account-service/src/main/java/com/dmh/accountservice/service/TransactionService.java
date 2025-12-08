package com.dmh.accountservice.service;

import com.dmh.accountservice.entity.Transaction;
import com.dmh.accountservice.exception.AccountNotFoundForTransactionException;
import com.dmh.accountservice.exception.NoTransactionsFoundException;
import com.dmh.accountservice.repository.AccountRepository;
import com.dmh.accountservice.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;

    public TransactionService(
            TransactionRepository transactionRepository,
            AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    public List<Transaction> findAllTransactionsByAccountId(Long accountId) {

        validateAccountExists(accountId);

        List<Transaction> transactions = transactionRepository
                .findAllByAccountIdOrderByTransactionDateDesc(accountId)
                .orElse(null);

        validateTransactionsExist(accountId, transactions);

        return transactions;
    }

    private void validateAccountExists(Long accountId) {
        boolean accountExists = accountRepository.existsById(accountId);

        if (!accountExists) {
            throw new AccountNotFoundForTransactionException(accountId);
        }
    }


    private void validateTransactionsExist(Long accountId, List<Transaction> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            throw new NoTransactionsFoundException(accountId);
        }
    }
}