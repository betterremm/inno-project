package com.innowise.userservice.exception;

public class CardLimitExceededException extends RuntimeException {
    public CardLimitExceededException() {
        super("User cannot have more than 5 payment cards");
    }
}
