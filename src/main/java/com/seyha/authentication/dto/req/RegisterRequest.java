package com.seyha.authentication.dto.req;

/**
 * Developed by ChhornSeyha
 * Date: 15/02/2026
 */

public record RegisterRequest(String firstname,
                              String lastname,
                              String email,
                              String password) {}