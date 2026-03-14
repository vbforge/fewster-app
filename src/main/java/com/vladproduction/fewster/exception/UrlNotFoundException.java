package com.vladproduction.fewster.exception;

import org.springframework.http.HttpStatus;
 
/**
 * Thrown when a URL record cannot be found — either by ID, short code, or
 * original URL — or when the authenticated user does not own the resource.
 * Maps to HTTP 404 Not Found.
 */
public class UrlNotFoundException extends FewsterException {
 
    public UrlNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
 
    public static UrlNotFoundException byId(Long id) {
        return new UrlNotFoundException("URL not found for ID: " + id);
    }
 
    public static UrlNotFoundException byIdForUser(Long id) {
        return new UrlNotFoundException("URL not found or access denied for ID: " + id);
    }
 
    public static UrlNotFoundException byShortUrl(String shortUrl) {
        return new UrlNotFoundException("No URL found for short code: " + shortUrl);
    }
}