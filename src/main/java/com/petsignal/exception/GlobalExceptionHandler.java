package com.petsignal.exception;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class GlobalExceptionHandler {


  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValidationExceptions(
      MethodArgumentNotValidException ex,
      HttpServletRequest request) {

    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult().getFieldErrors().forEach(error ->
        errors.put(error.getField(), error.getDefaultMessage())
    );

    String message = "Validation failed: " + String.join(", ", errors.values());

    ErrorResponse errorResponse = new ErrorResponse(
        BAD_REQUEST.value(),
        BAD_REQUEST.getReasonPhrase(),
        message,
        request.getRequestURI()
    );

    return new ResponseEntity<>(errorResponse, BAD_REQUEST);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolation(
      ConstraintViolationException ex,
      HttpServletRequest request) {

    String message = ex.getConstraintViolations().stream()
        .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
        .reduce((a, b) -> a + "; " + b)
        .orElse("Validation failed");

    ErrorResponse errorResponse = new ErrorResponse(
        BAD_REQUEST.value(),
        BAD_REQUEST.getReasonPhrase(),
        message,
        request.getRequestURI()
    );

    return new ResponseEntity<>(errorResponse, BAD_REQUEST);
  }

  @ExceptionHandler(UnsupportedFileTypeException.class)
  public ResponseEntity<ErrorResponse> handleUnsupportedFileType(
      UnsupportedFileTypeException ex,
      HttpServletRequest request) {

    ErrorResponse errorResponse = new ErrorResponse(
        BAD_REQUEST.value(),
        BAD_REQUEST.getReasonPhrase(),
        ex.getMessage(),
        request.getRequestURI()
    );

    return new ResponseEntity<>(errorResponse, BAD_REQUEST);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceNotFound(
      EntityNotFoundException ex,
      HttpServletRequest request) {

    ErrorResponse errorResponse = new ErrorResponse(
        NOT_FOUND.value(),
        NOT_FOUND.getReasonPhrase(),
        ex.getMessage(),
        request.getRequestURI()
    );

    return new ResponseEntity<>(errorResponse, NOT_FOUND);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGlobalException(
      Exception ex,
      HttpServletRequest request) {
    ErrorResponse errorResponse = new ErrorResponse(
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        "Internal Server Error",
        "An unexpected error occurred",
        request.getRequestURI()
    );

    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(S3BucketException.class)
  public ResponseEntity<ErrorResponse> handleS3StorageException(
      S3BucketException ex,
      HttpServletRequest request) {

    ErrorResponse errorResponse = new ErrorResponse(
        HttpStatus.INTERNAL_SERVER_ERROR.value(),
        "S3 Bucket Error",
        ex.getMessage(),
        request.getRequestURI()
    );

    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }
} 