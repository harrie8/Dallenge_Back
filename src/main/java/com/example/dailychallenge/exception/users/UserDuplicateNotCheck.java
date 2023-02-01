package com.example.dailychallenge.exception.users;

import com.example.dailychallenge.exception.DailyChallengeException;

public class UserDuplicateNotCheck extends DailyChallengeException {

    private static final String MESSAGE = "아이디 중복체크를 해주세요.";

    public UserDuplicateNotCheck() {
        super(MESSAGE);
    }

    @Override
    public String getMessage() {
        return MESSAGE;
    }

    @Override
    public int getStatusCode() {
        return 409;
    }
}
