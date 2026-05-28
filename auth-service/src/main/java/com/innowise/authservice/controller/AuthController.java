package com.innowise.authservice.controller;

import com.innowise.authservice.dto.request.LoginRequest;
import com.innowise.authservice.dto.request.RefreshTokenRequest;
import com.innowise.authservice.dto.request.SaveCredentialsRequest;
import com.innowise.authservice.dto.request.TokenRequest;
import com.innowise.authservice.dto.response.TokenResponse;
import com.innowise.authservice.dto.response.ValidateTokenResponse;
import com.innowise.authservice.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/credentials")
    public ResponseEntity<Void> saveCredentials(@Valid @RequestBody SaveCredentialsRequest request) {
        authService.saveCredentials(request);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping({"/login", "/token"})
    public ResponseEntity<TokenResponse> createToken(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/validate")
    public ResponseEntity<ValidateTokenResponse> validateToken(@Valid @RequestBody TokenRequest request) {
        return ResponseEntity.ok(authService.validateToken(request.token()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        return ResponseEntity.ok(authService.refreshToken(request));
    }
}
