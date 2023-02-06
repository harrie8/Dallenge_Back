package com.example.dailychallenge.exception.challenge;

import com.example.dailychallenge.exception.DailyChallengeException;

public class ChallengeNotFound extends DailyChallengeException {

    private static final String MESSAGE = "챌린지를 찾을 수 없습니다.";

    public ChallengeNotFound() {
        super(MESSAGE);
    }

    public ChallengeNotFound(Throwable cause) {
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
