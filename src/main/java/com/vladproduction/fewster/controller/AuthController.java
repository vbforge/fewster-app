package com.vladproduction.fewster.controller;

import com.vladproduction.fewster.dto.UserDTO;
import com.vladproduction.fewster.entity.User;
import com.vladproduction.fewster.exception.UserAlreadyExistsException;
import com.vladproduction.fewster.service.UserService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Handles registration and login page rendering.
 *
 * Changes from original:
 *   - Catches UserAlreadyExistsException specifically and surfaces a clear field
 *     error instead of the generic "Registration failed" message.
 *   - Login form POST is handled by Spring Security (/login processing URL);
 *     this controller only serves the GET for the login page.
 */
@Slf4j
@Controller
public class AuthController {

    @Value("${role.name}")
    private String role;

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // ── Login ──────────────────────────────────────────────────────────────────

    @GetMapping("/login")
    public String showLoginForm(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "auth/login";
    }

    // ── Register ───────────────────────────────────────────────────────────────

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("userDTO", new UserDTO());
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("userDTO") UserDTO userDTO,
                               BindingResult bindingResult,
                               RedirectAttributes redirectAttributes,
                               Model model) {

        if (bindingResult.hasErrors()) {
            return "auth/register";
        }

        try {
            User user = new User();
            user.setUsername(userDTO.getUsername());
            user.setPassword(userDTO.getPassword());
            user.setRole(role);

            userService.createUser(user);

            redirectAttributes.addFlashAttribute("success",
                    "Registration successful! Welcome to Fewster, " + userDTO.getUsername() + ".");
            return "redirect:/login";

        } catch (UserAlreadyExistsException ex) {
            // Surface as a field-level error so the form highlights the username input
            bindingResult.rejectValue("username", "username.taken",
                    "That username is already taken. Please choose another.");
            return "auth/register";

        } catch (Exception ex) {
            log.error("Unexpected error during registration for username: {}", userDTO.getUsername(), ex);
            model.addAttribute("error", "Registration failed due to an unexpected error. Please try again.");
            return "auth/register";
        }
    }
}
