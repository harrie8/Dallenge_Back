package com.example.dailychallenge.exception.userChallenge;

import com.example.dailychallenge.exception.DailyChallengeException;

public class UserChallengeNotFound extends DailyChallengeException {

    private static final String MESSAGE = "사용자의 참가를 찾을 수 없습니다.";

    public UserChallengeNotFound() {
        super(MESSAGE);
    }

    public UserChallengeNotFound(Throwable cause) {
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
