package com.vladproduction.fewster.exception;

import org.springframework.http.HttpStatus;

/**
 * Thrown when the short URL generation algorithm exhausts its retry budget
 * without producing a globally unique code.
 * Maps to HTTP 500 Internal Server Error.
 */
public class ShortUrlGenerationException extends FewsterException {

    public ShortUrlGenerationException(String message) {
        super(message, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
