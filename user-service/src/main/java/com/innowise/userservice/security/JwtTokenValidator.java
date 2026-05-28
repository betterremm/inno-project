package com.innowise.userservice.security;

import com.innowise.userservice.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

@Component
public class JwtTokenValidator {

    public static final String CLAIM_USER_ID = "userId";
    public static final String CLAIM_ROLE = "role";
    public static final String CLAIM_TOKEN_TYPE = "tokenType";

    private final SecretKey secretKey;

    public JwtTokenValidator(JwtProperties properties) {
        this.secretKey = Keys.hmacShaKeyFor(properties.secret().getBytes(StandardCharsets.UTF_8));
    }

    public AuthenticatedUser parseAccessToken(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        if (!"ACCESS".equals(claims.get(CLAIM_TOKEN_TYPE, String.class))) {
            throw new JwtException("Invalid token type");
        }
        Long userId = claims.get(CLAIM_USER_ID, Long.class);
        Role role = Role.valueOf(claims.get(CLAIM_ROLE, String.class));
        return new AuthenticatedUser(userId, role);
    }
}
