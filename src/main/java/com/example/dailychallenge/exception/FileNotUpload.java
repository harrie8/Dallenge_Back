package com.example.dailychallenge.exception;

public class FileNotUpload extends DailyChallengeException {

    private static final String MESSAGE = "파일을 업로드 할 수 없습니다.";

    public FileNotUpload() {
        super(MESSAGE);
    }

    public FileNotUpload(Throwable cause) {
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
