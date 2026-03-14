package com.vladproduction.fewster.exception;

import org.springframework.http.HttpStatus;
 
/**
 * Thrown when attempting to register or rename to a username that is already taken.
 * Maps to HTTP 409 Conflict.
 */
public class UserAlreadyExistsException extends FewsterException {
 
    public UserAlreadyExistsException(String username) {
        super("Username is already taken: " + username, HttpStatus.CONFLICT);
    }
}