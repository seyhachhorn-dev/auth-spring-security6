package com.seyha.authentication.service;

import com.seyha.authentication.dto.req.AuthenticationRequest;
import com.seyha.authentication.dto.req.RegisterRequest;
import com.seyha.authentication.dto.res.AuthenticationResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/**
 * Developed by ChhornSeyha
 * Date: 15/02/2026
 */

public interface AuthenticationService {
    String register(RegisterRequest req);
    AuthenticationResponse authenticate(AuthenticationRequest req);
}
