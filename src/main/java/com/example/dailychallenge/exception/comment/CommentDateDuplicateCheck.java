package com.example.dailychallenge.exception.comment;

import com.example.dailychallenge.exception.DailyChallengeException;

public class CommentDateDuplicateCheck extends DailyChallengeException {
    private static final String MESSAGE = "오늘은 해당 챌린지에 더이상 댓글을 작성할 수 없습니다.";

    public CommentDateDuplicateCheck() {
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
