package com.vladproduction.fewster.exception;

import org.springframework.http.HttpStatus;
 
/**
 * Thrown when a URL fails format validation (null, blank, or missing http/https).
 * Maps to HTTP 400 Bad Request.
 */
public class InvalidUrlException extends FewsterException {
 
    public InvalidUrlException(String url) {
        super("Invalid URL format — must start with http:// or https://: " + url, HttpStatus.BAD_REQUEST);
    }
}