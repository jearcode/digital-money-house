package com.dmh.accountservice.exception;

public class AccountNotFoundException extends RuntimeException {

    private AccountNotFoundException(String message) {
        super(message);
    }

    public static AccountNotFoundException byId(Long id) {
        return new AccountNotFoundException("Account with id " + id + " not found.");
    }

    public static AccountNotFoundException byUserId(Long id) {
        return new AccountNotFoundException("Account with id " + id + " not found.");
    }

    public static AccountNotFoundException byCvu(String cvu) {
        return new AccountNotFoundException("Account with CVU " + cvu + " not found.");
    }

    public static AccountNotFoundException byAlias(String alias) {
        return new AccountNotFoundException("Account with alias " + alias + " not found.");
    }

}