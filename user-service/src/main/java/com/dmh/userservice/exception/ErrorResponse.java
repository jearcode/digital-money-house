package com.dmh.userservice.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Standard error response format for all API errors.
 */
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    /**
     * HTTP status code (e.g., 404, 409, 500)
     */
    private int status;

    /**
     * Error code/identifier (e.g., "USER_NOT_FOUND", "VALIDATION_FAILED")
     * Useful for client-side error handling
     */
    private String error;

    /**
     * User-friendly error message
     * Should be clear and actionable
     */
    private String message;

    /**
     * Timestamp when the error occurred
     * Useful for debugging and logs
     */
    private LocalDateTime timestamp;

    /**
     * The request path that caused the error
     */
    private String path;

    /**
     * HTTP method used (GET, POST, PATCH, etc)
     */
    private String method;

    /**
     * Validation errors per field
     * Example: { "email": "Email must be valid", "firstName": "First name is required" }
     * Only included if there are validation errors
     */
    private Map<String, String> validationErrors;

    /**
     * Retry-after information for rate limit errors
     * Can be a number of seconds or a date
     * Only included for 429 Too Many Requests responses
     */
    private String retryAfter;
}