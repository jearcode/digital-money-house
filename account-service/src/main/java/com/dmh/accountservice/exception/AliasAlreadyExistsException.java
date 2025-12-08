package com.dmh.accountservice.exception;

public class AliasAlreadyExistsException extends RuntimeException {

    public AliasAlreadyExistsException(String alias) {
        super("Alias: " + alias + " already exists.");
    }
}
