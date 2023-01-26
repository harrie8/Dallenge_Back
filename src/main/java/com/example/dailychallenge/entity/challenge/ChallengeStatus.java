package com.example.dailychallenge.entity.challenge;

import lombok.Getter;

@Getter
public enum ChallengeStatus {
    SUCCESS(true, "성공"),
    FAILURE(false, "실패"),
    TRYING(false, "도전중"),
    PAUSE(false, "중지"),
    ;

    private final boolean isSuccess;
    private final String description;

    ChallengeStatus(boolean isSuccess, String description) {
        this.isSuccess = isSuccess;
        this.description = description;
    }
}
