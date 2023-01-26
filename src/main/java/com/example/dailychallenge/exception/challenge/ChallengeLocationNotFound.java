package com.example.dailychallenge.exception.challenge;

import com.example.dailychallenge.exception.DailyChallengeException;

public class ChallengeLocationNotFound extends DailyChallengeException {

    private static final String MESSAGE = "존재하지 않는 챌린지 장소입니다.";

    public ChallengeLocationNotFound() {
        super(MESSAGE);
    }

    public ChallengeLocationNotFound(Throwable cause) {
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
