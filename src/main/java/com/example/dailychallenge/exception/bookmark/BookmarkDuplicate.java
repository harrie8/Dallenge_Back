package com.example.dailychallenge.exception.bookmark;

import com.example.dailychallenge.exception.DailyChallengeException;

public class BookmarkDuplicate extends DailyChallengeException {

    private static final String MESSAGE = "이미 북마크한 챌린지입니다.";

    public BookmarkDuplicate() {
        super(MESSAGE);
    }

    public BookmarkDuplicate(Throwable cause) {
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
