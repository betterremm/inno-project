package com.innowise.userservice.security;

public record AuthenticatedUser(Long userId, Role role) {
}
