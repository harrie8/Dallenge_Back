package com.example.dailychallenge.exception;

public class CommonException extends DailyChallengeException {

    private final String message;

    public CommonException(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
