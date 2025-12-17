package com.dmh.accountservice.exception;

public class NoTransactionFoundException extends RuntimeException {
    public NoTransactionFoundException(Long accountId) {
        super("No transaction found for account with id " + accountId + ".");
    }
}
