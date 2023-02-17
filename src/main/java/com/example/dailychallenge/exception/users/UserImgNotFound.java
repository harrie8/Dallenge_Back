package com.example.dailychallenge.exception.users;

import com.example.dailychallenge.exception.DailyChallengeException;

public class UserImgNotFound extends DailyChallengeException {

    private static final String MESSAGE = "회원 이미지를 찾을 수 없습니다.";

    public UserImgNotFound() {
        super(MESSAGE);
    }

    public UserImgNotFound(Throwable cause) {
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
