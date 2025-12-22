package com.dmh.accountservice.exception;

public class InsufficientFundsException extends RuntimeException {
    public InsufficientFundsException(String message) {
        super(message);
    }
    public InsufficientFundsException() {
        super("Insufficient balance for this transaction.");
    }
}
