package com.innowise.authservice.service;

import com.innowise.authservice.config.JwtProperties;
import com.innowise.authservice.dto.request.LoginRequest;
import com.innowise.authservice.dto.request.SaveCredentialsRequest;
import com.innowise.authservice.entity.UserCredentialEntity;
import com.innowise.authservice.model.Role;
import com.innowise.authservice.model.TokenType;
import com.innowise.authservice.repository.UserCredentialRepository;
import com.innowise.authservice.security.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserCredentialRepository credentialRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties("test-jwt-secret-key-min-32-chars-long", 15, 7);
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(properties);
        authService = new AuthService(credentialRepository, passwordEncoder, jwtTokenProvider, properties);
    }

    @Test
    void loginReturnsAccessAndRefreshTokens() {
        UserCredentialEntity credential = new UserCredentialEntity();
        credential.setUserId(1L);
        credential.setLogin("alice");
        credential.setPasswordHash("hash");
        credential.setRole(Role.USER);
        when(credentialRepository.findByLogin("alice")).thenReturn(Optional.of(credential));
        when(passwordEncoder.matches("secret", "hash")).thenReturn(true);

        var tokens = authService.login(new LoginRequest("alice", "secret"));

        assertThat(tokens.accessToken()).isNotBlank();
        assertThat(tokens.refreshToken()).isNotBlank();
        assertThat(tokens.tokenType()).isEqualTo("Bearer");
    }

    @Test
    void validateAccessToken() {
        UserCredentialEntity saved = new UserCredentialEntity();
        saved.setUserId(5L);
        saved.setLogin("bob");
        saved.setPasswordHash("hash");
        saved.setRole(Role.ADMIN);
        when(passwordEncoder.encode(anyString())).thenReturn("hash");
        when(credentialRepository.existsByLogin(anyString())).thenReturn(false);
        when(credentialRepository.existsByUserId(any())).thenReturn(false);
        when(credentialRepository.save(any())).thenReturn(saved);
        when(credentialRepository.findByLogin("bob")).thenReturn(Optional.of(saved));
        when(passwordEncoder.matches("secret", "hash")).thenReturn(true);

        authService.saveCredentials(new SaveCredentialsRequest(5L, "bob", "secret", Role.ADMIN));
        var tokens = authService.login(new LoginRequest("bob", "secret"));

        var validation = authService.validateToken(tokens.accessToken());
        assertThat(validation.valid()).isTrue();
        assertThat(validation.userId()).isEqualTo(5L);
        assertThat(validation.role()).isEqualTo(Role.ADMIN);
    }

    @Test
    void refreshTokenIssuesNewPair() {
        UserCredentialEntity credential = new UserCredentialEntity();
        credential.setUserId(2L);
        credential.setLogin("carol");
        credential.setPasswordHash("hash");
        credential.setRole(Role.USER);
        when(credentialRepository.findByLogin("carol")).thenReturn(Optional.of(credential));
        when(passwordEncoder.matches("pass", "hash")).thenReturn(true);

        var tokens = authService.login(new LoginRequest("carol", "pass"));
        var refreshed = authService.refreshToken(new com.innowise.authservice.dto.request.RefreshTokenRequest(tokens.refreshToken()));

        assertThat(refreshed.accessToken()).isNotBlank();
        assertThat(jwtTokenProvider().extractTokenType(refreshed.refreshToken())).isEqualTo(TokenType.REFRESH);
    }

    private JwtTokenProvider jwtTokenProvider() {
        return new JwtTokenProvider(new JwtProperties("test-jwt-secret-key-min-32-chars-long", 15, 7));
    }
}
