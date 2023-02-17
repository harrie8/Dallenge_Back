package com.example.dailychallenge.exception.comment;

import com.example.dailychallenge.exception.DailyChallengeException;

public class CommentNotFound extends DailyChallengeException {

    private static final String MESSAGE = "댓글을 찾을 수 없습니다.";

    public CommentNotFound() {
        super(MESSAGE);
    }

    public CommentNotFound(Throwable cause) {
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
