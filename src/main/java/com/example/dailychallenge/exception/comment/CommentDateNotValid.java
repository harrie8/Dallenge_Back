package com.example.dailychallenge.exception.comment;

import com.example.dailychallenge.exception.DailyChallengeException;

public class CommentDateNotValid  extends DailyChallengeException {
    private static final String MESSAGE = "이미 댓글을 작성하였습니다.";

    public CommentDateNotValid() {
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
