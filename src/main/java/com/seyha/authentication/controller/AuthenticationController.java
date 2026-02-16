package com.seyha.authentication.controller;

import com.seyha.authentication.dto.req.AuthenticationRequest;
import com.seyha.authentication.dto.req.RegisterRequest;
import com.seyha.authentication.dto.res.AuthenticationResponse;
import com.seyha.authentication.service.AuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

/**
 * Developed by ChhornSeyha
 * Date: 15/02/2026
 */

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequest req){
        return ResponseEntity.ok(authenticationService.register(req));
    }

    @PostMapping("/authenticate")
    public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest req){
        return ResponseEntity.ok(authenticationService.authenticate(req));
    }

//    @PostMapping("/refresh-token")
//    public void refreshToken(HttpServletRequest request,
//                             HttpServletResponse response) throws IOException {
//       authenticationService.refreshToken(request,response);
//    }

}
