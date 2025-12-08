package com.dmh.accountservice.exception;

public class CardNotFoundException extends RuntimeException {

    public CardNotFoundException(Long cardId, Long accountId) {
        super("Card with id " + cardId + " not found for account " + accountId);
    }

}