package com.example.dailychallenge.exception.bookmark;

import com.example.dailychallenge.exception.DailyChallengeException;

public class BookmarkNotFound extends DailyChallengeException {

    private static final String MESSAGE = "북마크를 찾을 수 없습니다.";

    public BookmarkNotFound() {
        super(MESSAGE);
    }

    public BookmarkNotFound(Throwable cause) {
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
