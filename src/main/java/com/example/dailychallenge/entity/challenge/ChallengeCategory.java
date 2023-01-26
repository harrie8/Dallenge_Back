package com.example.dailychallenge.entity.challenge;

import com.example.dailychallenge.exception.challenge.ChallengeCategoryNotFound;
import java.util.Arrays;
import lombok.Getter;

@Getter
public enum ChallengeCategory {
    STUDY("공부"),
    VOLUNTEER("봉사"),
    WORKOUT("운동"),
    HEALTH("건강"),
    ECONOMY("경제"),
    ;

    private final String description;

    ChallengeCategory(String description) {
        this.description = description;
    }

    public static ChallengeCategory findByDescription(String description) {
        return Arrays.stream(values())
                .filter(challengeCategory -> challengeCategory.isSameDescription(description))
                .findAny()
                .orElseThrow(ChallengeCategoryNotFound::new);
    }

    private boolean isSameDescription(String description) {
        return this.description.equals(description);
    }
}
