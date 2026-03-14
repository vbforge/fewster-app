package com.vladproduction.fewster.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * DTO for the "change username" settings form.
 * Bound to POST /settings/username.
 */
@Getter
@Setter
public class ChangeUsernameDTO {

    @NotBlank(message = "New username is required")
    @Size(min = 5, max = 20, message = "Username must be between 5 and 20 characters")
    @Pattern(
            regexp = "^[a-zA-Z0-9_]+$",
            message = "Username may only contain letters, digits, and underscores"
    )
    private String newUsername;
}