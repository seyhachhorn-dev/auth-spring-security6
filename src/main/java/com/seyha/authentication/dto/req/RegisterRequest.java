package com.seyha.authentication.dto.req;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Developed by ChhornSeyha
 * Date: 15/02/2026
 */

public record RegisterRequest(@NotBlank(message = "First name is required")
                              String firstname,

                              @NotBlank(message = "Last name is required")
                              String lastname,

                              @Email(message = "Invalid email format")
                              @NotBlank
                              String email,

                              @Size(min = 8, message = "Password must be at least 8 chars")
                              String password
) {}