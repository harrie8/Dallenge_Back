package com.example.dailychallenge.exception.badge;

import com.example.dailychallenge.exception.DailyChallengeException;

public class BadgeTypeNotFound extends DailyChallengeException {

    private static final String MESSAGE = "존재하지 않는 뱃지 타입입니다.";

    public BadgeTypeNotFound() {
        super(MESSAGE);
    }

    public BadgeTypeNotFound(Throwable cause) {
        super(MESSAGE, cause);
    }

    @Override
    public String getMessage() {
        return MESSAGE;
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
