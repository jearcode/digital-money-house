package com.dmh.accountservice.exception;

public class NoTransactionsFoundException extends RuntimeException {

    public NoTransactionsFoundException(String message) {
        super(message);
    }

    public NoTransactionsFoundException(Long accountId) {
        super("No transactions found for account with id " + accountId + ".");
    }

    public NoTransactionsFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}