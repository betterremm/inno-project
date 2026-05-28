package com.innowise.authservice.dto.response;

import com.innowise.authservice.model.Role;

public record ValidateTokenResponse(
        boolean valid,
        Long userId,
        Role role
) {
}
