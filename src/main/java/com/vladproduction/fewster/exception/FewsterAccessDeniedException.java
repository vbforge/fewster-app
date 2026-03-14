package com.vladproduction.fewster.exception;

import org.springframework.http.HttpStatus;
 
/**
 * Thrown when an authenticated user attempts to read or modify a resource
 * that belongs to a different user.
 * Maps to HTTP 403 Forbidden.
 *
 * Note: named FewsterAccessDeniedException to avoid clashing with
 * Spring Security's org.springframework.security.access.AccessDeniedException.
 */
public class FewsterAccessDeniedException extends FewsterException {
 
    public FewsterAccessDeniedException(String message) {
        super(message, HttpStatus.FORBIDDEN);
    }
 
    public static FewsterAccessDeniedException forUrl(Long urlId) {
        return new FewsterAccessDeniedException(
                "Access denied — you do not own the URL with ID: " + urlId);
    }
}