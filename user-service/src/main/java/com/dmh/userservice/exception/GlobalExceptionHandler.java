package com.dmh.userservice.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.HtmlUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(
            UserNotFoundException ex,
            HttpServletRequest request) {

        logger.warn("User not found: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error("USER_NOT_FOUND")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(HtmlUtils.htmlEscape(request.getRequestURI()))
                .method(request.getMethod())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(UserAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyExist(
            UserAlreadyExistException ex,
            HttpServletRequest request) {

        logger.warn("User already exists: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .error("USER_ALREADY_EXISTS")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(HtmlUtils.htmlEscape(request.getRequestURI()))
                .method(request.getMethod())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }


    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFound(
            AccountNotFoundException ex,
            HttpServletRequest request) {

        logger.warn("Account not found: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .error("ACCOUNT_NOT_FOUND")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(HtmlUtils.htmlEscape(request.getRequestURI()))
                .method(request.getMethod())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }


    @ExceptionHandler(RateLimitException.class)
    public ResponseEntity<ErrorResponse> handleRateLimit(
            RateLimitException ex,
            HttpServletRequest request) {

        logger.warn("Rate limit exceeded: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.TOO_MANY_REQUESTS.value())
                .error("RATE_LIMIT_EXCEEDED")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(HtmlUtils.htmlEscape(request.getRequestURI()))
                .method(request.getMethod())
                .retryAfter(ex.getRetryAfter())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.TOO_MANY_REQUESTS);
    }

    @ExceptionHandler(InvalidDniFormatException.class)
    public ResponseEntity<ErrorResponse> handleInvalidDniFormat(
            InvalidDniFormatException ex,
            HttpServletRequest request) {

        logger.warn("Invalid DNI format: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("INVALID_DNI_FORMAT")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(HtmlUtils.htmlEscape(request.getRequestURI()))
                .method(request.getMethod())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex,
            HttpServletRequest request) {

        logger.warn("Validation error: {}", ex.getMessage());

        Map<String, String> fieldErrors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        error -> error.getDefaultMessage(),
                        (existing, replacement) -> existing
                ));

        Map<String, String> globalErrors = new HashMap<>();
        ex.getBindingResult().getGlobalErrors().forEach(error ->
                globalErrors.put(error.getObjectName(), error.getDefaultMessage())
        );

        Map<String, String> allErrors = new HashMap<>();
        allErrors.putAll(fieldErrors);
        allErrors.putAll(globalErrors);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("VALIDATION_FAILED")
                .message("Validation failed for request body")
                .timestamp(LocalDateTime.now())
                .path(HtmlUtils.htmlEscape(request.getRequestURI()))
                .method(request.getMethod())
                .validationErrors(allErrors)
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDeniedException(
            AccessDeniedException ex,
            HttpServletRequest request) {

        logger.warn("Access denied: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.FORBIDDEN.value())
                .error("ACCESS_DENIED")
                .message("You do not have permission to access this resource.")
                .timestamp(LocalDateTime.now())
                .path(HtmlUtils.htmlEscape(request.getRequestURI()))
                .method(request.getMethod())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(IdentityProviderException.class)
    public ResponseEntity<ErrorResponse> handleIdentityProviderException(
            IdentityProviderException ex,
            HttpServletRequest request) {

        logger.error("Identity provider error: {} - {}", ex.getProvider(), ex.getOperation(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("IDENTITY_PROVIDER_ERROR")
                .message("An error occurred while communicating with the identity provider: " + ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(HtmlUtils.htmlEscape(request.getRequestURI()))
                .method(request.getMethod())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AccountServiceException.class)
    public ResponseEntity<ErrorResponse> handleAccountServiceException(
            AccountServiceException ex,
            HttpServletRequest request) {

        logger.error("Account service error: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_GATEWAY.value())
                .error("ACCOUNT_SERVICE_UNAVAILABLE")
                .message("The account service is temporarily unavailable: " + ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(HtmlUtils.htmlEscape(request.getRequestURI()))
                .method(request.getMethod())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_GATEWAY);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(
            Exception ex,
            HttpServletRequest request) {

        logger.error("Unexpected error: {}", ex.getMessage(), ex);

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .error("INTERNAL_SERVER_ERROR")
                .message("An unexpected error occurred. Please try again later.")
                .timestamp(LocalDateTime.now())
                .path(HtmlUtils.htmlEscape(request.getRequestURI()))
                .method(request.getMethod())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request) {

        logger.warn("Malformed JSON request: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .error("MALFORMED_JSON")
                .message("Malformed JSON request or syntax error: " + ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(HtmlUtils.htmlEscape(request.getRequestURI()))
                .method(request.getMethod())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

}