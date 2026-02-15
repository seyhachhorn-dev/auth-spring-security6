package com.seyha.authentication.dto.req;

/**
 * Developed by ChhornSeyha
 * Date: 15/02/2026
 */

public record AuthenticationRequest(
        String email,
        String password
) {
}
