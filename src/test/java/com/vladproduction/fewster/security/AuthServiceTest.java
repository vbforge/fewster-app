package com.vladproduction.fewster.security;

import com.vladproduction.fewster.entity.User;
import com.vladproduction.fewster.exception.UserNotFoundException;
import com.vladproduction.fewster.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionRegistry;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SessionRegistry sessionRegistry;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    private AuthService authService;
    private User testUser;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository);

        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setPassword("encodedPassword");
        testUser.setRole("USER");
    }

    @Test
    void getCurrentUser_WhenAuthenticated_ReturnsUser() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        SecurityContextHolder.setContext(securityContext);

        // Act
        User result = authService.getCurrentUser();

        // Assert
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        assertEquals(1L, result.getId());
    }

    @Test
    void getCurrentUser_WhenNotAuthenticated_ThrowsException() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.getCurrentUser());
        assertEquals("No authenticated user in the current security context", exception.getMessage());
    }

    @Test
    void getCurrentUser_WhenUserNotFound_ThrowsException() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("nonexistentuser");
        when(userRepository.findByUsername("nonexistentuser")).thenReturn(Optional.empty());
        SecurityContextHolder.setContext(securityContext);

        // Act & Assert
        RuntimeException exception = assertThrows(UserNotFoundException.class,
                () -> authService.getCurrentUser());
        assertTrue(exception.getMessage().contains("User not found"));
    }

    @Test
    void getCurrentUsername_WhenAuthenticated_ReturnsUsername() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("testuser");
        SecurityContextHolder.setContext(securityContext);

        // Act
        String result = authService.getCurrentUsername();

        // Assert
        assertEquals("testuser", result);
    }

    @Test
    void getCurrentUsername_WhenNotAuthenticated_ReturnsNull() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(null);
        SecurityContextHolder.setContext(securityContext);

        // Act
        String result = authService.getCurrentUsername();

        // Assert
        assertNull(result);
    }

    @Test
    void isCurrentUserOwner_WhenOwner_ReturnsTrue() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        SecurityContextHolder.setContext(securityContext);

        // Act
        boolean result = authService.isCurrentUserOwner(1L);

        // Assert
        assertTrue(result);
    }

    @Test
    void isCurrentUserOwner_WhenNotOwner_ReturnsFalse() {
        // Arrange
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("testuser");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(testUser));
        SecurityContextHolder.setContext(securityContext);

        // Act
        boolean result = authService.isCurrentUserOwner(999L);

        // Assert
        assertFalse(result);
    }

    @Test
    void getDemoUser_WhenDemoUserExists_ReturnsUser() {
        // Arrange
        User demoUser = new User();
        demoUser.setUsername("demouser");
        when(userRepository.findByUsername("demouser")).thenReturn(Optional.of(demoUser));

        // Act
        User result = authService.getDemoUser();

        // Assert
        assertNotNull(result);
        assertEquals("demouser", result.getUsername());
    }

}
