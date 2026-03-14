package com.vladproduction.fewster.exception;

import org.springframework.http.HttpStatus;
 
/**
 * Thrown when a user tries to shorten a URL they have already shortened.
 * The existing short URL is returned instead, but callers that need to
 * distinguish "created" from "already existed" can catch this.
 * Maps to HTTP 409 Conflict.
 */
public class DuplicateUrlException extends FewsterException {
 
    private final String existingShortUrl;
 
    public DuplicateUrlException(String originalUrl, String existingShortUrl) {
        super("URL already shortened: " + originalUrl, HttpStatus.CONFLICT);
        this.existingShortUrl = existingShortUrl;
    }
 
    public String getExistingShortUrl() {
        return existingShortUrl;
    }
}