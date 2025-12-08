package com.dmh.accountservice.exception;

public class AccountNotFoundForTransactionException extends RuntimeException {

    public AccountNotFoundForTransactionException(String message) {
        super(message);
    }

    public AccountNotFoundForTransactionException(Long accountId) {
        super("Account with id " + accountId + " not found. Cannot retrieve transactions.");
    }

    public AccountNotFoundForTransactionException(String message, Throwable cause) {
        super(message, cause);
    }
}