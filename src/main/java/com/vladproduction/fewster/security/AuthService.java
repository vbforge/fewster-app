package com.vladproduction.fewster.security;

import com.vladproduction.fewster.entity.User;
import com.vladproduction.fewster.exception.UserNotFoundException;
import com.vladproduction.fewster.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Returns the currently authenticated {@link User} entity.
     * Throws {@link UserNotFoundException} if the principal is not found in the database,
     * or a plain {@link RuntimeException} if no authentication is present at all
     * (which would indicate a misconfigured security filter).
     */
    public User getCurrentUser() {
        String username = getCurrentUsername();

        if (username == null) {
            throw new RuntimeException("No authenticated user in the current security context");
        }

        return userRepository.findByUsername(username)
                .orElseThrow(() -> UserNotFoundException.byUsername(username));
    }

    /**
     * Returns the demo user, used for unauthenticated URL shortening on the home page.
     */
    public User getDemoUser() {
        return userRepository.findByUsername("demouser")
                .orElseThrow(() -> UserNotFoundException.byUsername("demouser"));
    }

    /**
     * Returns the username of the currently authenticated principal, or {@code null}
     * if no authentication exists.
     */
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return null;
        }

        return authentication.getName();
    }

    /**
     * Returns {@code true} if the currently authenticated user owns the given resource.
     */
    public boolean isCurrentUserOwner(Long resourceOwnerId) {
        User currentUser = getCurrentUser();
        return currentUser.getId().equals(resourceOwnerId);
    }
}
