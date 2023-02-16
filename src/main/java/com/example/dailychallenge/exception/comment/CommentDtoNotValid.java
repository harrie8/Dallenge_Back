package com.example.dailychallenge.exception.comment;

import com.example.dailychallenge.exception.DailyChallengeException;

public class CommentDtoNotValid extends DailyChallengeException {

    private static final String MESSAGE = "댓글은 내용 또는 이미지가 필요합니다.";
    private String message;

    public CommentDtoNotValid() {
        super(MESSAGE);
        this.message = MESSAGE;
    }

    public CommentDtoNotValid(String message) {
        super(message);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
