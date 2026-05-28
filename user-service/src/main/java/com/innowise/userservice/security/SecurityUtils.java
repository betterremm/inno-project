package com.innowise.userservice.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static AuthenticatedUser currentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedUser user)) {
            throw new org.springframework.security.authentication.AuthenticationCredentialsNotFoundException(
                    "User is not authenticated");
        }
        return user;
    }

    public static boolean isAdmin() {
        return currentUser().role() == Role.ADMIN;
    }

    public static Long currentUserId() {
        return currentUser().userId();
    }
}
