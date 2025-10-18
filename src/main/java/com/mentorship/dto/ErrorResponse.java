package com.mentorship.dto;

import java.time.Instant;

/**
 * A standard, structured error response for the API.
 * @param timestamp When the error occurred.
 * @param status The HTTP status code.
 * @param error The HTTP status reason phrase (e.g., "Not Found").
 * @param message A developer-friendly message describing the error.
 * @param path The path where the error occurred.
 */
public record ErrorResponse(
    Instant timestamp,
    int status,
    String error,
    String message,
    String path
) {}