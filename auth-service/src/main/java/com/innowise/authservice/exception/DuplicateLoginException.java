package com.innowise.authservice.exception;

public class DuplicateLoginException extends RuntimeException {

    public DuplicateLoginException(String login) {
        super("Login already exists: " + login);
    }
}
