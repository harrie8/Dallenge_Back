package com.example.dailychallenge.exception.users;

import com.example.dailychallenge.exception.DailyChallengeException;

public class UserLoginFailure extends DailyChallengeException {

    private static final String MESSAGE = "아이디 또는 비밀번호를 잘못 입력했습니다";

    public UserLoginFailure() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 403;
    }

    @Override
    public String getMessage() {
        return super.getMessage();
    }
}
