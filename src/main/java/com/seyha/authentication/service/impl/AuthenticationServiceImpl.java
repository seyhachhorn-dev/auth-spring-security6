package com.seyha.authentication.service.impl;

import com.seyha.authentication.core.security.JwtService;
import com.seyha.authentication.core.security.SecurityUser;
import com.seyha.authentication.domain.RefreshToken;
import com.seyha.authentication.domain.User;
import com.seyha.authentication.dto.req.AuthenticationRequest;
import com.seyha.authentication.dto.req.RegisterRequest;
import com.seyha.authentication.dto.res.AuthenticationResponse;
import com.seyha.authentication.enums.Role;
import com.seyha.authentication.repository.RefreshTokenRepository;
import com.seyha.authentication.repository.UserRepository;
import com.seyha.authentication.service.AuthenticationService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

/**
 * Developed by ChhornSeyha
 * Date: 15/02/2026
 */

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    @Value("${security.jwt.expiration}")
    private  long jwtExpiration;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;



    @Override
    public String register(RegisterRequest req) {
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
        return "User registered successfully!";    }

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
        // Revoke old tokens to ensure strict security (Optional but recommended)
        revokeAllUserToken(user);
        // 3. Generate Token
        var jwtToken = jwtService.generateToken(new SecurityUser(user));
        var refreshToken = generateAndSaveRefreshToken(user);
        return  AuthenticationResponse.builder()
                .accessToken(jwtToken)
                .tokenType("Bearer ")
                .refreshToken(refreshToken)
                .expiresIn(jwtExpiration / 1000)
                .build();
    }


    private String generateAndSaveRefreshToken(User user) {
        var refreshToken = RefreshToken.builder()
                .user(user)
                .token(UUID.randomUUID().toString())
                .expiryDate(Instant.now().plusMillis(604800000)) // 7 days
                .revoked(false)
                .build();

        refreshTokenRepository.save(refreshToken);

        return refreshToken.getToken();
    }
    // 2. Revoking tokens (Remove .setExpired(true))
    private void revokeAllUserToken(User user){
        var validUserTokens = refreshTokenRepository.findAllValidTokenByUser(user.getId());
        if(validUserTokens.isEmpty()){
            return;
        }
        validUserTokens.forEach(t -> t.setRevoked(true));
        refreshTokenRepository.saveAll(validUserTokens);
    }

}
