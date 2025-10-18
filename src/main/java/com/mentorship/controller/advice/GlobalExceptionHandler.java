package com.mentorship.controller.advice;

import com.mentorship.dto.ErrorResponse;
import com.mentorship.exception.ConflictException;
import com.mentorship.exception.ForbiddenException;
import com.mentorship.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex, HttpServletRequest request) {
    log.warn("Resource not found: {} (Request: {} {})", ex.getMessage(), request.getMethod(), request.getRequestURI());
    ErrorResponse errorResponse = new ErrorResponse(
            Instant.now(),
            HttpStatus.NOT_FOUND.value(),
            "Not Found",
            ex.getMessage(),
            request.getRequestURI()
    );
    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(ConflictException.class)
  public ResponseEntity<ErrorResponse> handleConflict(ConflictException ex, HttpServletRequest request) {
    log.warn("Conflict detected: {} (Request: {} {})", ex.getMessage(), request.getMethod(), request.getRequestURI());
    ErrorResponse errorResponse = new ErrorResponse(
            Instant.now(),
            HttpStatus.CONFLICT.value(),
            "Conflict",
            ex.getMessage(),
            request.getRequestURI()
    );
    return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
  }

  @ExceptionHandler(ForbiddenException.class)
  public ResponseEntity<ErrorResponse> handleForbidden(ForbiddenException ex, HttpServletRequest request) {
    log.warn("Forbidden access: {} (Request: {} {})", ex.getMessage(), request.getMethod(), request.getRequestURI());
    ErrorResponse errorResponse = new ErrorResponse(
            Instant.now(),
            HttpStatus.FORBIDDEN.value(),
            "Forbidden",
            ex.getMessage(),
            request.getRequestURI()
    );
    return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
    log.warn("Bad request: {} (Request: {} {})", ex.getMessage(), request.getMethod(), request.getRequestURI());
    ErrorResponse errorResponse = new ErrorResponse(
            Instant.now(),
            HttpStatus.BAD_REQUEST.value(),
            "Bad Request",
            ex.getMessage(),
            request.getRequestURI()
    );
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
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

    ErrorResponse errorResponse = new ErrorResponse(
            Instant.now(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "Internal Server Error",
            "An unexpected internal server error occurred. Please try again later.", // Hide internal details
            request.getRequestURI()
    );
    return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
  }
}