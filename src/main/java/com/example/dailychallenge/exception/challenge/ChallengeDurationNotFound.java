package com.example.dailychallenge.exception.challenge;

import com.example.dailychallenge.exception.DailyChallengeException;

public class ChallengeDurationNotFound extends DailyChallengeException {

    private static final String MESSAGE = "존재하지 않는 챌린지 기간입니다.";

    public ChallengeDurationNotFound() {
        super(MESSAGE);
    }

    public ChallengeDurationNotFound(Throwable cause) {
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
