package com.example.dailychallenge.exception.challenge;

import com.example.dailychallenge.exception.DailyChallengeException;

public class ChallengeCategoryNotFound extends DailyChallengeException {

    private static final String MESSAGE = "존재하지 않는 챌린지 카테고리입니다.";

    public ChallengeCategoryNotFound() {
        super(MESSAGE);
    }

    public ChallengeCategoryNotFound(Throwable cause) {
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
