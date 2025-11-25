package com.dmh.accountservice.exception;

public class UserAlreadyHasAccountException extends RuntimeException {
    public UserAlreadyHasAccountException(String message) {
        super(message);
    }
}
