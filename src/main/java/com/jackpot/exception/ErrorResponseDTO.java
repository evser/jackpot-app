package com.jackpot.exception;

import java.time.LocalDateTime;

/**
 * A standard DTO for returning error details to the client.
 *
 * @param timestamp The time the error occurred.
 * @param status    The HTTP status code.
 * @param error     The HTTP status reason (e.g., "Not Found").
 * @param message   A user-friendly error message.
 * @param path      The request URI that caused the error.
 */
public record ErrorResponseDTO(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path
) {
}
