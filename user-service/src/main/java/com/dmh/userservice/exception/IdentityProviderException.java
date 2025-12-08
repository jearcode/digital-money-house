package com.dmh.userservice.exception;

public class IdentityProviderException extends RuntimeException {
    private final String provider;
    private final String operation;

    public IdentityProviderException(String provider, String operation, String message) {
        super("Error in " + provider + " while " + operation + ": " + message);
        this.provider = provider;
        this.operation = operation;
    }

    public IdentityProviderException(String provider, String operation, Throwable cause) {
        super("Error in " + provider + " while " + operation, cause);
        this.provider = provider;
        this.operation = operation;
    }

    public String getProvider() {
        return provider;
    }

    public String getOperation() {
        return operation;
    }
}
