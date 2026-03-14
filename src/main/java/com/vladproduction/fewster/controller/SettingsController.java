package com.vladproduction.fewster.controller;

import com.vladproduction.fewster.dto.ChangePasswordDTO;
import com.vladproduction.fewster.dto.ChangeUsernameDTO;
import com.vladproduction.fewster.entity.User;
import com.vladproduction.fewster.exception.UserAlreadyExistsException;
import com.vladproduction.fewster.security.AuthService;
import com.vladproduction.fewster.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Handles account settings: change username and change password.
 *
 * Both operations require the user to be authenticated (enforced in SecurityConfig).
 *
 * Username change:  POST /settings/username
 *   - Validates the new username via ChangeUsernameDTO
 *   - Checks uniqueness (UserAlreadyExistsException → shown as field error)
 *   - Re-authenticates the session with the new username so the navbar stays correct
 *
 * Password change:  POST /settings/password
 *   - Verifies currentPassword against the stored BCrypt hash
 *   - Validates newPassword strength via ChangePasswordDTO
 *   - Checks newPassword == confirmPassword
 *   - Forces logout after success so the user re-authenticates with the new password
 */
@Slf4j
@Controller
@RequestMapping("/settings")
public class SettingsController {

    private final UserService userService;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    public SettingsController(UserService userService,
                              AuthService authService,
                              PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
    }

    // ── GET /settings ──────────────────────────────────────────────────────────

    @GetMapping
    public String settingsPage(Model model) {
        populateModel(model);
        return "settings/settings";
    }

    // ── POST /settings/username ────────────────────────────────────────────────

    @PostMapping("/username")
    public String changeUsername(@Valid @ModelAttribute("changeUsernameDTO") ChangeUsernameDTO dto,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {

        if (bindingResult.hasErrors()) {
            populateModel(model);
            return "settings/settings";
        }

        User currentUser = authService.getCurrentUser();

        // Prevent no-op update
        if (currentUser.getUsername().equals(dto.getNewUsername())) {
            model.addAttribute("usernameError", "New username is the same as your current username.");
            populateModel(model);
            return "settings/settings";
        }

        try {
            userService.updateUsername(currentUser.getId(), dto.getNewUsername());

            // Re-authenticate with the new username so the navbar and security
            // context reflect the change without forcing a logout
            Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
            Authentication newAuth = new UsernamePasswordAuthenticationToken(
                    dto.getNewUsername(),
                    currentAuth.getCredentials(),
                    currentAuth.getAuthorities()
            );
            SecurityContextHolder.getContext().setAuthentication(newAuth);

            log.info("Username updated: '{}' → '{}'", currentUser.getUsername(), dto.getNewUsername());
            redirectAttributes.addFlashAttribute("successUsername",
                    "Username updated to \"" + dto.getNewUsername() + "\" successfully.");

        } catch (UserAlreadyExistsException ex) {
            model.addAttribute("usernameError", "That username is already taken. Please choose another.");
            populateModel(model);
            return "settings/settings";
        }

        return "redirect:/settings";
    }

    // ── POST /settings/password ────────────────────────────────────────────────

    @PostMapping("/password")
    public String changePassword(@Valid @ModelAttribute("changePasswordDTO") ChangePasswordDTO dto,
                                 BindingResult bindingResult,
                                 RedirectAttributes redirectAttributes,
                                 Model model,
                                 HttpServletRequest request,
                                 HttpServletResponse response) {

        if (bindingResult.hasErrors()) {
            populateModel(model);
            return "settings/settings";
        }

        User currentUser = authService.getCurrentUser();

        // Verify current password matches stored hash
        if (!passwordEncoder.matches(dto.getCurrentPassword(), currentUser.getPassword())) {
            model.addAttribute("passwordError", "Current password is incorrect.");
            populateModel(model);
            return "settings/settings";
        }

        // newPassword and confirmPassword must match
        if (!dto.getNewPassword().equals(dto.getConfirmPassword())) {
            model.addAttribute("passwordError", "New password and confirmation do not match.");
            populateModel(model);
            return "settings/settings";
        }

        // Prevent reuse of the same password
        if (passwordEncoder.matches(dto.getNewPassword(), currentUser.getPassword())) {
            model.addAttribute("passwordError", "New password must be different from your current password.");
            populateModel(model);
            return "settings/settings";
        }

        userService.updatePassword(currentUser.getId(), dto.getNewPassword());
        log.info("Password updated for user: '{}'", currentUser.getUsername());

        // Invalidate session — user must log in again with the new password
        new SecurityContextLogoutHandler().logout(request, response,
                SecurityContextHolder.getContext().getAuthentication());

        redirectAttributes.addFlashAttribute("success",
                "Password updated successfully. Please log in with your new password.");
        return "redirect:/login";
    }

    // ── Helper ─────────────────────────────────────────────────────────────────

    /**
     * Populates both form-backing objects so the page can render even when
     * only one of the two forms has a validation error.
     */
    private void populateModel(Model model) {
        if (!model.containsAttribute("changeUsernameDTO")) {
            model.addAttribute("changeUsernameDTO", new ChangeUsernameDTO());
        }
        if (!model.containsAttribute("changePasswordDTO")) {
            model.addAttribute("changePasswordDTO", new ChangePasswordDTO());
        }
        // Pass the current username so the page can show it
        model.addAttribute("currentUsername", authService.getCurrentUsername());
    }
}