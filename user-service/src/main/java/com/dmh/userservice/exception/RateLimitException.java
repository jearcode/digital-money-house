package com.dmh.userservice.exception;

public class RateLimitException extends RuntimeException {
    private final String retryAfter;

    public RateLimitException(String message) {
        super(message);
        this.retryAfter = null;
    }

    public RateLimitException(String message, String retryAfter) {
        super(message);
        this.retryAfter = retryAfter;
    }

    public RateLimitException(String message, Throwable cause) {
        super(message, cause);
        this.retryAfter = null;
    }

    public String getRetryAfter() {
        return retryAfter;
    }
}
