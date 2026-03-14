package com.vladproduction.fewster.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Data transfer object for user registration.
 *
 * Used only for the registration form (POST /register).
 * Password is write-only — never serialised in JSON responses.
 */
@Getter
@Setter
public class UserDTO {

    @NotBlank(message = "Username is required")
    @Size(min = 5, max = 20, message = "Username must be between 5 and 20 characters")
    @Pattern(
            regexp = "^[a-zA-Z0-9_]+$",
            message = "Username may only contain letters, digits, and underscores"
    )
    private String username;

    @NotBlank(message = "Password is required")
    @Size(min = 5, max = 64, message = "Password must be between 5 and 64 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
            message = "Password must contain at least one lowercase letter, one uppercase letter, and one digit"
    )
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
}
