package com.example.dailychallenge.exception.userChallenge;

import com.example.dailychallenge.exception.DailyChallengeException;

public class UserChallengeDuplicate extends DailyChallengeException {

    private static final String MESSAGE = "이미 참가한 챌린지입니다.";
    public UserChallengeDuplicate() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }

    @Override
    public String getMessage() {
        return MESSAGE;
    }
}
