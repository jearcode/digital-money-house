package com.dmh.userservice.exception;

public class InvalidDniFormatException extends RuntimeException {

    public InvalidDniFormatException(String dni) {
        super("Invalid DNI format: " + dni + ". DNI must be 8-20 alphanumeric characters.");
    }

    public InvalidDniFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}