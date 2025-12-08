package com.dmh.accountservice.exception;

public class CardAlreadyExistException extends RuntimeException {

    public CardAlreadyExistException(String message) {
        super(message);
    }

}