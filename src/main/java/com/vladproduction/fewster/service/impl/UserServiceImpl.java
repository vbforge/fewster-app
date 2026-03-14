package com.vladproduction.fewster.service.impl;

import com.vladproduction.fewster.entity.User;
import com.vladproduction.fewster.exception.UserAlreadyExistsException;
import com.vladproduction.fewster.exception.UserNotFoundException;
import com.vladproduction.fewster.repository.UserRepository;
import com.vladproduction.fewster.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void createUser(User user) {
        log.info("Registering new user: '{}'", user.getUsername());

        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new UserAlreadyExistsException(user.getUsername());
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);

        log.info("User '{}' registered successfully", user.getUsername());
    }

    @Override
    @Transactional
    public void updateUsername(Long userId, String newUsername) {
        log.info("Updating username for user ID: {} → '{}'", userId, newUsername);

        if (userRepository.findByUsername(newUsername).isPresent()) {
            throw new UserAlreadyExistsException(newUsername);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.byId(userId));

        user.setUsername(newUsername);
        userRepository.save(user);

        log.info("Username updated to '{}' for user ID: {}", newUsername, userId);
    }

    @Override
    @Transactional
    public void updatePassword(Long userId, String newRawPassword) {
        log.info("Updating password for user ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> UserNotFoundException.byId(userId));

        user.setPassword(passwordEncoder.encode(newRawPassword));
        userRepository.save(user);

        log.info("Password updated for user ID: {}", userId);
    }
}
