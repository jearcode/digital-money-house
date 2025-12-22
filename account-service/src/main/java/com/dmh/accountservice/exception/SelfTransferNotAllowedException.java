package com.dmh.accountservice.exception;

public class SelfTransferNotAllowedException extends RuntimeException {
    public SelfTransferNotAllowedException(String destination) {
        super("You cannot transfer funds to your own account: " + destination);
    }
}
