package by.betterremm.userservice.exception;

public class DuplicateEmailException extends RuntimeException {
    public DuplicateEmailException(String email) {
        super("User with email already exists: " + email);
    }
}
