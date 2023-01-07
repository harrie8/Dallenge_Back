package com.example.dailychallenge.exception;

import java.util.HashMap;
import java.util.Map;

public abstract class DailyChallengeException extends RuntimeException {

    private final Map<String, String> validation = new HashMap<>();

    public DailyChallengeException(String message) {
        super(message);
    }

    public DailyChallengeException(String message, Throwable cause) {
        super(message, cause);
    }

    public abstract int getStatusCode();

    public void addValidation(String fieldName, String message) {
        validation.put(fieldName, message);
    }
}
