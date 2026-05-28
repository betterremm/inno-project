package com.innowise.userservice.exception;

public class InactiveUserException extends RuntimeException {
    public InactiveUserException(Long userId) {
        super("User is inactive: " + userId);
    }
}
