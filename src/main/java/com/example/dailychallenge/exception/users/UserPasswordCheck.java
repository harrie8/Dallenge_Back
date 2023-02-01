package com.example.dailychallenge.exception.users;

import com.example.dailychallenge.exception.DailyChallengeException;

public class UserPasswordCheck extends DailyChallengeException {

    private static final String MESSAGE = "비밀번호를 다시 확인해주세요.";

    public UserPasswordCheck() {
        super(MESSAGE);
    }

    @Override
    public String getMessage() {
        return MESSAGE;
    }

    @Override
    public int getStatusCode() {
        return 403;
    }
}
