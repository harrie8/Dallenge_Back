package com.example.dailychallenge.exception.hashtag;

import com.example.dailychallenge.exception.DailyChallengeException;

public class HashtagDtoBlank extends DailyChallengeException {

    private static final String MESSAGE = "해시태그 값은 비어서는 안 됩니다.";

    public HashtagDtoBlank() {
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
