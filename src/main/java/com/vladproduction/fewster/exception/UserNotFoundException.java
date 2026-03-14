package com.vladproduction.fewster.exception;

import org.springframework.http.HttpStatus;
 
/**
 * Thrown when a user cannot be found by username or ID.
 * Maps to HTTP 404 Not Found.
 */
public class UserNotFoundException extends FewsterException {
 
    public UserNotFoundException(String message) {
        super(message, HttpStatus.NOT_FOUND);
    }
 
    public static UserNotFoundException byUsername(String username) {
        return new UserNotFoundException("User not found: " + username);
    }
 
    public static UserNotFoundException byId(Long id) {
        return new UserNotFoundException("User not found for ID: " + id);
    }
}