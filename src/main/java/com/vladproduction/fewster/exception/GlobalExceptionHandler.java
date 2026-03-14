package com.vladproduction.fewster.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Centralised exception → HTTP response mapping.
 *
 * Architecture:
 *   - All domain exceptions extend FewsterException and carry their own HttpStatus.
 *     A single handler method covers every subclass — no per-exception methods needed.
 *   - Infrastructure exceptions (Spring Security, JPA, Bean Validation) each have
 *     their own handler so we can return a useful message rather than a stack trace.
 *   - The catch-all Exception handler is declared last and returns 500.
 *
 * Ordering: most-specific → least-specific (Spring picks the first match).
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String TIMESTAMP = "timestamp";
    private static final String STATUS    = "status";
    private static final String ERROR     = "error";
    private static final String MESSAGE   = "message";

    // ── Domain exceptions (all FewsterException subclasses) ───────────────────

    /**
     * Single handler for every FewsterException subclass.
     * Each subclass carries its own HttpStatus — no duplication required.
     */
    @ExceptionHandler(FewsterException.class)
    public ResponseEntity<Map<String, Object>> handleFewsterException(FewsterException ex) {
        log.error("Domain exception [{}]: {}", ex.getClass().getSimpleName(), ex.getMessage());
        return buildResponse(ex.getHttpStatus(), ex.getClass().getSimpleName(), ex.getMessage());
    }

    // ── Bean validation (@Valid on controller method arguments) ───────────────

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(MethodArgumentNotValidException ex) {
        String details = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining("; "));
        log.warn("Validation failed: {}", details);
        return buildResponse(HttpStatus.BAD_REQUEST, "Validation Error", details);
    }

    // ── Spring Security ────────────────────────────────────────────────────────

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<Map<String, Object>> handleSpringAccessDenied(AccessDeniedException ex) {
        log.warn("Spring Security access denied: {}", ex.getMessage());
        return buildResponse(HttpStatus.FORBIDDEN, "Forbidden", "You do not have permission to perform this action.");
    }

    // ── JPA / database ────────────────────────────────────────────────────────

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.error("Data integrity violation: {}", ex.getMostSpecificCause().getMessage());
        return buildResponse(HttpStatus.CONFLICT, "Data Conflict",
                "The request conflicts with existing data (e.g. duplicate username or URL).");
    }

    // ── Illegal argument (legacy, kept for safety) ────────────────────────────

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, "Bad Request", ex.getMessage());
    }

    // ── Catch-all (must be last) ───────────────────────────────────────────────

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(Exception ex) {
        log.error("Unexpected error", ex);
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error",
                "An unexpected error occurred. Please try again later.");
    }

    // ── Helper ─────────────────────────────────────────────────────────────────

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String error, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put(TIMESTAMP, LocalDateTime.now());
        body.put(STATUS, status.value());
        body.put(ERROR, error);
        body.put(MESSAGE, message);
        return ResponseEntity.status(status).body(body);
    }
}
