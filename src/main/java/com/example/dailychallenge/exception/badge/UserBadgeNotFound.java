package com.example.dailychallenge.exception.badge;

import com.example.dailychallenge.exception.DailyChallengeException;

public class UserBadgeNotFound extends DailyChallengeException {

    private static final String MESSAGE = "존재하지 않는 유저 뱃지입니다.";

    public UserBadgeNotFound() {
        super(MESSAGE);
    }

    public UserBadgeNotFound(Throwable cause) {
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
