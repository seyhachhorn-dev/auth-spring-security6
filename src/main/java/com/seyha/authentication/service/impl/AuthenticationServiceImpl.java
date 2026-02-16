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
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

/**
 * Developed by ChhornSeyha
 * Date: 15/02/2026
 */

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
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
       var savedUser = userRepository.save(user);
        // 3. Generate Token (Wrap entity in Adapter first)
        var jwtToken = jwtService.generateToken(new SecurityUser(user));
        var refreshToken = generateAndSaveRefreshToken(savedUser);
        return new AuthenticationResponse(jwtToken,refreshToken);
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
        // Revoke old tokens to ensure strict security (Optional but recommended)
        revokeAllUserToken(user);
        // 3. Generate Token
        var jwtToken = jwtService.generateToken(new SecurityUser(user));
        var refreshToken = generateAndSaveRefreshToken(user);
        return new AuthenticationResponse(jwtToken,refreshToken);
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

    public void refreshToken(HttpServletRequest req, HttpServletResponse res) throws IOException {
        final String authHeader = req.getHeader(HttpHeaders.AUTHORIZATION);
        final String refreshToken;
        final String userEmail;

        if(authHeader == null || !authHeader.startsWith("Bearer ")){
            return;
        }
        // 2. Extract refreshToken
        refreshToken = authHeader.substring(7);
        userEmail = jwtService.extractUsername(refreshToken);

        if(userEmail!=null){
            var user = this.userRepository.findByEmail(userEmail).orElseThrow();

            // 5. VALIDATE THE TOKEN
            var isTokenValid = jwtService.isTokenValid(refreshToken, new SecurityUser(user));
            var isTokenRevoked = refreshTokenRepository.findByToken(refreshToken)
                    .map(RefreshToken::isRevoked)
                    .orElse(true);

            if(isTokenValid && !isTokenRevoked){
                // 6. ROTATION LOGIC (The "Magic")
                // Revoke the OLD refresh token (So it can't be used again)
                var storedToken = refreshTokenRepository.findByToken(refreshToken).orElseThrow();
                storedToken.setRevoked(true);
                refreshTokenRepository.save(storedToken);

                //7 generate new
                var jwtToken = jwtService.generateToken(new SecurityUser(user));
                var newRefreshToken = generateAndSaveRefreshToken(user);

                // 8 send response

                var authResponse = new AuthenticationResponse(jwtToken,newRefreshToken);
                new ObjectMapper().writeValue(res.getOutputStream(),authResponse);
            }
        }
    }
}
