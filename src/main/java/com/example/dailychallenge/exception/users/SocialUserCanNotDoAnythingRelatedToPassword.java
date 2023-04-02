package com.example.dailychallenge.exception.users;

import com.example.dailychallenge.exception.DailyChallengeException;

public class SocialUserCanNotDoAnythingRelatedToPassword extends DailyChallengeException {

    private static final String MESSAGE = "소셜 유저는 비밀번호와 관련된 작업을 수행할 수 없습니다.";

    public SocialUserCanNotDoAnythingRelatedToPassword() {
        super(MESSAGE);
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
