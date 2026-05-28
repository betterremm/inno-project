package com.innowise.authservice.service;

import com.innowise.authservice.config.JwtProperties;
import com.innowise.authservice.dto.request.LoginRequest;
import com.innowise.authservice.dto.request.RefreshTokenRequest;
import com.innowise.authservice.dto.request.SaveCredentialsRequest;
import com.innowise.authservice.dto.response.TokenResponse;
import com.innowise.authservice.dto.response.ValidateTokenResponse;
import com.innowise.authservice.entity.UserCredentialEntity;
import com.innowise.authservice.exception.DuplicateLoginException;
import com.innowise.authservice.exception.InvalidCredentialsException;
import com.innowise.authservice.exception.InvalidTokenException;
import com.innowise.authservice.model.Role;
import com.innowise.authservice.model.TokenType;
import com.innowise.authservice.repository.UserCredentialRepository;
import com.innowise.authservice.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class AuthService {

    private final UserCredentialRepository credentialRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtProperties jwtProperties;

    public AuthService(UserCredentialRepository credentialRepository,
                       PasswordEncoder passwordEncoder,
                       JwtTokenProvider jwtTokenProvider,
                       JwtProperties jwtProperties) {
        this.credentialRepository = credentialRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtProperties = jwtProperties;
    }

    @Transactional
    public void saveCredentials(SaveCredentialsRequest request) {
        if (credentialRepository.existsByLogin(request.login())) {
            throw new DuplicateLoginException(request.login());
        }
        if (credentialRepository.existsByUserId(request.userId())) {
            throw new DuplicateLoginException("userId=" + request.userId());
        }
        UserCredentialEntity entity = new UserCredentialEntity();
        entity.setUserId(request.userId());
        entity.setLogin(request.login());
        entity.setPasswordHash(passwordEncoder.encode(request.password()));
        entity.setRole(request.role());
        entity.setCreatedAt(Instant.now());
        entity.setUpdatedAt(Instant.now());
        credentialRepository.save(entity);
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {
        UserCredentialEntity credential = credentialRepository.findByLogin(request.login())
                .orElseThrow(InvalidCredentialsException::new);
        if (!passwordEncoder.matches(request.password(), credential.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }
        return buildTokenPair(credential.getUserId(), credential.getRole());
    }

    @Transactional(readOnly = true)
    public ValidateTokenResponse validateToken(String token) {
        if (!jwtTokenProvider.isValid(token)) {
            return new ValidateTokenResponse(false, null, null);
        }
        if (jwtTokenProvider.extractTokenType(token) != TokenType.ACCESS) {
            return new ValidateTokenResponse(false, null, null);
        }
        return new ValidateTokenResponse(
                true,
                jwtTokenProvider.extractUserId(token),
                jwtTokenProvider.extractRole(token)
        );
    }

    @Transactional(readOnly = true)
    public TokenResponse refreshToken(RefreshTokenRequest request) {
        String refreshToken = request.refreshToken();
        if (!jwtTokenProvider.isValid(refreshToken)) {
            throw new InvalidTokenException("Invalid refresh token");
        }
        if (jwtTokenProvider.extractTokenType(refreshToken) != TokenType.REFRESH) {
            throw new InvalidTokenException("Token is not a refresh token");
        }
        Long userId = jwtTokenProvider.extractUserId(refreshToken);
        Role role = jwtTokenProvider.extractRole(refreshToken);
        return buildTokenPair(userId, role);
    }

    private TokenResponse buildTokenPair(Long userId, Role role) {
        return new TokenResponse(
                jwtTokenProvider.createAccessToken(userId, role),
                jwtTokenProvider.createRefreshToken(userId, role),
                "Bearer",
                jwtProperties.accessExpirationMinutes() * 60
        );
    }
}
