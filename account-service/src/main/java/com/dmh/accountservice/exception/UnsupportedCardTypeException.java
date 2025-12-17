package com.dmh.accountservice.exception;

import com.dmh.accountservice.model.enums.CardType;

public class UnsupportedCardTypeException extends RuntimeException {
    public UnsupportedCardTypeException(CardType cardType) {
        super("Unsupported card: " + cardType);;
    }
}
