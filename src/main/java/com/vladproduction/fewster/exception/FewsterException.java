package com.vladproduction.fewster.exception;

import org.springframework.http.HttpStatus;
 
/**
 * Base exception for all Fewster application exceptions.
 *
 * Every custom exception carries an HttpStatus so GlobalExceptionHandler
 * can extract the correct response code without needing a separate handler
 * method per exception class.
 */
public abstract class FewsterException extends RuntimeException {
 
    private final HttpStatus httpStatus;
 
    protected FewsterException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
 
    protected FewsterException(String message, HttpStatus httpStatus, Throwable cause) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }
 
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}