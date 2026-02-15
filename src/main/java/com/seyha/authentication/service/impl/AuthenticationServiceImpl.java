package com.seyha.authentication.service.impl;

import com.seyha.authentication.core.security.JwtService;
import com.seyha.authentication.core.security.SecurityUser;
import com.seyha.authentication.domain.User;
import com.seyha.authentication.dto.req.AuthenticationRequest;
import com.seyha.authentication.dto.req.RegisterRequest;
import com.seyha.authentication.dto.res.AuthenticationResponse;
import com.seyha.authentication.enums.Role;
import com.seyha.authentication.repository.UserRepository;
import com.seyha.authentication.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Developed by ChhornSeyha
 * Date: 15/02/2026
 */

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;


    @Override
    public AuthenticationResponse register(RegisterRequest req) {
//        create user entity
        var user = User.builder()
                .email(req.email())
                .firstname(req.firstname())
                .lastname(req.lastname())
                .password(passwordEncoder.encode(req.password())) // Hash password!
                .role(Role.USER) //by default
                .enabled(true)
                .build();
//        save into db
        userRepository.save(user);
        // 3. Generate Token (Wrap entity in Adapter first)
        var jwtToken = jwtService.generateToken(new SecurityUser(user));
        return new AuthenticationResponse(jwtToken);
    }

    @Override
    public AuthenticationResponse authenticate(AuthenticationRequest req) {
        // 1. Delegate authentication to the Manager (checks password)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        req.email(),
                        req.password()
                )
        );
        // 2. If valid, fetch user
        var user = userRepository.findByEmail(req.email()).orElseThrow();
        // 3. Generate Token
        var jwtToken = jwtService.generateToken(new SecurityUser(user));
        return new AuthenticationResponse(jwtToken);
    }
}
