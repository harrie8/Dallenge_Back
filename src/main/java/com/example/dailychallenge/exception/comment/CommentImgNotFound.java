package com.example.dailychallenge.exception.comment;

import com.example.dailychallenge.exception.DailyChallengeException;

public class CommentImgNotFound extends DailyChallengeException {

    private static final String MESSAGE = "댓글 이미지를 찾을 수 없습니다.";

    public CommentImgNotFound() {
        super(MESSAGE);
    }

    public CommentImgNotFound(Throwable cause) {
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
