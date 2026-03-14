package com.vladproduction.fewster.service;

import com.vladproduction.fewster.entity.User;

public interface UserService {

    /**
     * Register a new user. Throws {@link com.vladproduction.fewster.exception.UserAlreadyExistsException}
     * if the username is already taken.
     */
    void createUser(User user);

    /**
     * Change the username for an existing user.
     * Throws {@link com.vladproduction.fewster.exception.UserAlreadyExistsException} if the new username
     * is already taken, or {@link com.vladproduction.fewster.exception.UserNotFoundException} if the user ID
     * does not exist.
     */
    void updateUsername(Long userId, String newUsername);

    /**
     * Change the password for an existing user. The raw password is BCrypt-encoded before saving.
     * Throws {@link com.vladproduction.fewster.exception.UserNotFoundException} if the user ID does not exist.
     */
    void updatePassword(Long userId, String newRawPassword);
}
