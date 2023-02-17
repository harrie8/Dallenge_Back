package com.example.dailychallenge.exception.hashtag;

import com.example.dailychallenge.exception.DailyChallengeException;

public class HashTagNotFound extends DailyChallengeException {

    private static final String MESSAGE = "해시태그를 찾을 수 없습니다.";

    public HashTagNotFound() {
        super(MESSAGE);
    }

    public HashTagNotFound(Throwable cause) {
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
