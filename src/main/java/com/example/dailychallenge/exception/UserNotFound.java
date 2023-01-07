package com.example.dailychallenge.exception;

public class UserNotFound extends DailyChallengeException {

    private static final String MESSAGE = "존재하지 않는 회원입니다.";

    public UserNotFound() {
        super(MESSAGE);
    }

    public UserNotFound(Throwable cause) {
        super(MESSAGE, cause);
    }

    @Override
    public String getMessage() {
        return MESSAGE;
    }

    @Override
    public int getStatusCode() {
        return 404;
    }
}
