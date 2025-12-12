package com.dmh.accountservice.exception;

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

    @ExceptionHandler(AccountNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFound(
            AccountNotFoundException ex,
            HttpServletRequest request) {

        logger.warn("Account not found: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error("ACCOUNT_NOT_FOUND")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(HtmlUtils.htmlEscape(request.getRequestURI()))
                .method(request.getMethod())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CardNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleCardNotFound(
            CardNotFoundException ex,
            HttpServletRequest request) {

        logger.warn("Card not found: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error("CARD_NOT_FOUND")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(HtmlUtils.htmlEscape(request.getRequestURI()))
                .method(request.getMethod())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }


    @ExceptionHandler(UserAlreadyHasAccountException.class)
    public ResponseEntity<ErrorResponse> handleUserAlreadyHasAccount(
            UserAlreadyHasAccountException ex,
            HttpServletRequest request) {

        logger.warn("User already has account: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .error("USER_ALREADY_HAS_ACCOUNT")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(HtmlUtils.htmlEscape(request.getRequestURI()))
                .method(request.getMethod())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(CardAlreadyExistException.class)
    public ResponseEntity<ErrorResponse> handleCardAlreadyExist(
            CardAlreadyExistException ex,
            HttpServletRequest request) {

        logger.warn("Card already exists: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .error("CARD_ALREADY_EXISTS")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(HtmlUtils.htmlEscape(request.getRequestURI()))
                .method(request.getMethod())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
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

    @ExceptionHandler(AccountNotFoundForTransactionException.class)
    public ResponseEntity<ErrorResponse> handleAccountNotFoundForTransaction(
            AccountNotFoundForTransactionException ex,
            HttpServletRequest request) {

        logger.warn("Account not found for transaction: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error("ACCOUNT_NOT_FOUND_FOR_TRANSACTION")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(HtmlUtils.htmlEscape(request.getRequestURI()))
                .method(request.getMethod())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NoTransactionsFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoTransactionsFound(
            NoTransactionsFoundException ex,
            HttpServletRequest request) {

        logger.warn("No transactions found: {}", ex.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error("NO_TRANSACTIONS_FOUND")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .path(HtmlUtils.htmlEscape(request.getRequestURI()))
                .method(request.getMethod())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AliasAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleAliasAlreadyExists(
            AliasAlreadyExistsException e,
            HttpServletRequest request) {

        logger.warn("Conflict: ", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .error("ALIAS_ALREADY_EXISTS")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .path(HtmlUtils.htmlEscape(request.getRequestURI()))
                .method(request.getMethod())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(NoTransactionFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoTransactionFound (NoTransactionFoundException e,
                                                                   HttpServletRequest request) {
        logger.warn("Not Found", e.getMessage());

        ErrorResponse errorResponse = ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .error("NO_TRANSACTION_FOUND")
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .path(HtmlUtils.htmlEscape(request.getRequestURI()))
                .method(request.getMethod())
                .build();

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

}