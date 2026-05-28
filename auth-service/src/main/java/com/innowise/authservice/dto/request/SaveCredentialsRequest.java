package com.innowise.authservice.dto.request;

import com.innowise.authservice.model.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SaveCredentialsRequest(
        @NotNull Long userId,
        @NotBlank String login,
        @NotBlank String password,
        @NotNull Role role
) {
}
