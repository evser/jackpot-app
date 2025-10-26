package com.jackpot.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

/**
 * Global Exception Handler for the application.
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles EntityNotFoundException, typically thrown by services when a resource
     * (e.g., Jackpot, JackpotContribution) is not found.
     *
     * @param ex      The thrown EntityNotFoundException.
     * @param request The web request.
     * @return A ResponseEntity with a 404 NOT_FOUND status.
     */
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDTO handleEntityNotFound(EntityNotFoundException ex, WebRequest request) {
        log.warn("Resource not found: {}", ex.getMessage());
        return new ErrorResponseDTO(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                HttpStatus.NOT_FOUND.getReasonPhrase(),
                ex.getMessage(), // This message is safe (e.g., "Bet contribution not found.")
                getPath(request)
        );
    }

    /**
     * Handles MethodArgumentNotValidException, which occurs when @Valid DTO validation fails.
     *
     * @param ex      The thrown MethodArgumentNotValidException.
     * @param request The web request.
     * @return A ResponseEntity with a 400 BAD_REQUEST status.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleValidationErrors(MethodArgumentNotValidException ex, WebRequest request) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> "'" + error.getField() + "': " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.warn("Validation failed for request {}: {}", getPath(request), message);
        return new ErrorResponseDTO(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Validation failed: " + message,
                getPath(request)
        );
    }

    /**
     * Handles MethodArgumentTypeMismatchException, e.g., passing "abc" for a Long path variable.
     *
     * @param ex      The thrown MethodArgumentTypeMismatchException.
     * @param request The web request.
     * @return A ResponseEntity with a 400 BAD_REQUEST status.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDTO handleTypeMismatch(MethodArgumentTypeMismatchException ex, WebRequest request) {
        String requiredType = ex.getRequiredType() != null ? ex.getRequiredType().getSimpleName() : "unknown";
        String message = String.format("Parameter '%s' with value '%s' could not be converted to type '%s'.",
                ex.getName(), ex.getValue(), requiredType);

        log.warn("Parameter type mismatch for request {}: {}", getPath(request), message);
        return new ErrorResponseDTO(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                message,
                getPath(request)
        );
    }


    /**
     * A generic catch-all handler for any other unhandled exceptions.
     * Returns a 500 INTERNAL_SERVER_ERROR.
     *
     * @param ex      The thrown Exception.
     * @param request The web request.
     * @return A ResponseEntity with a 500 INTERNAL_SERVER_ERROR status.
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponseDTO handleGenericException(Exception ex, WebRequest request) {
        log.error("Unhandled exception for request {}: {}", getPath(request), ex.getMessage(), ex);

        String message = "An unexpected internal error occurred.";
        return new ErrorResponseDTO(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
                message,
                getPath(request)
        );
    }

    /**
     * Helper method to extract the request URI from the WebRequest.
     */
    private String getPath(WebRequest request) {
        if (request instanceof ServletWebRequest) {
            return ((ServletWebRequest) request).getRequest().getRequestURI();
        }
        return request.getDescription(false).replace("uri=", "");
    }
}
