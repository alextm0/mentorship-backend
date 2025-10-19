package com.mentorship.controller.advice;

import com.mentorship.dto.ErrorResponse;
import com.mentorship.exception.ConflictException;
import com.mentorship.exception.ForbiddenException;
import com.mentorship.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
    log.warn("Resource not found: {} (Request: {} {})", ex.getMessage(), request.getMethod(), request.getRequestURI());
    return buildResponseEntity(HttpStatus.NOT_FOUND, ex.getMessage(), request);
  }

  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex, HttpServletRequest request) {
    log.warn("Conflict detected: {} (Request: {} {})", ex.getMessage(), request.getMethod(), request.getRequestURI());
    return buildResponseEntity(HttpStatus.CONFLICT, ex.getMessage(), request);
  }

  @ExceptionHandler(ForbiddenException.class)
  public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenException ex, HttpServletRequest request) {
    log.warn("Forbidden access: {} (Request: {} {})", ex.getMessage(), request.getMethod(), request.getRequestURI());
    return buildResponseEntity(HttpStatus.FORBIDDEN, ex.getMessage(), request);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
    log.warn("Bad request: {} (Request: {} {})", ex.getMessage(), request.getMethod(), request.getRequestURI());
    return buildResponseEntity(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpServletRequest request) {
    String message = ex.getBindingResult().getAllErrors().stream()
            .map(error -> {
              if (error instanceof FieldError fieldError) {
                return fieldError.getField() + ": " + fieldError.getDefaultMessage();
              }
              return error.getDefaultMessage();
            })
            .collect(Collectors.joining("; "));
    log.warn("Validation failed: {} (Request: {} {})", message, request.getMethod(), request.getRequestURI());
    return buildResponseEntity(HttpStatus.BAD_REQUEST, message, request);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest request) {
    String message = ex.getConstraintViolations().stream()
            .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
            .collect(Collectors.joining("; "));
    log.warn("Constraint violation: {} (Request: {} {})", message, request.getMethod(), request.getRequestURI());
    return buildResponseEntity(HttpStatus.BAD_REQUEST, message, request);
  }

  /**
   * Fallback handler for any other unhandled exception.
   * This is a critical safety net.
   */
  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, HttpServletRequest request) {
    // Log unexpected errors at the ERROR level with the full stack trace.
    // This is for your monitoring/alerting system.
    log.error("Unhandled exception occurred (Request: {} {})", request.getMethod(), request.getRequestURI(), ex);

    return buildResponseEntity(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected internal server error occurred. Please try again later.",
            request
    );
  }

  private ResponseEntity<ErrorResponse> buildResponseEntity(HttpStatus status, String message, HttpServletRequest request) {
    ErrorResponse errorResponse = new ErrorResponse(
            Instant.now(),
            status.value(),
            status.getReasonPhrase(),
            message,
            request.getRequestURI()
    );
    return new ResponseEntity<>(errorResponse, status);
  }
}
