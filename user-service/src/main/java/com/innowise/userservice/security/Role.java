package com.innowise.userservice.security;

public enum Role {
    ADMIN,
    USER;

    public String authority() {
        return "ROLE_" + name();
    }
}
