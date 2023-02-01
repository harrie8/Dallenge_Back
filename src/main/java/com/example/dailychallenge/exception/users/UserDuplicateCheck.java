package com.example.dailychallenge.exception.users;

import com.example.dailychallenge.exception.DailyChallengeException;

public class UserDuplicateCheck extends DailyChallengeException {

    private static final String MESSAGE = "이미 가입된 회원입니다.";
    public UserDuplicateCheck() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 409;
    }

    @Override
    public String getMessage() {
        return MESSAGE;
    }
}
