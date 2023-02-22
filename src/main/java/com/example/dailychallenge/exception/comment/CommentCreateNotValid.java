package com.example.dailychallenge.exception.comment;

import com.example.dailychallenge.exception.DailyChallengeException;

public class CommentCreateNotValid extends DailyChallengeException {

    private static final String MESSAGE = "댓글은 내용 또는 이미지가 필요합니다.";

    public CommentCreateNotValid() {
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
