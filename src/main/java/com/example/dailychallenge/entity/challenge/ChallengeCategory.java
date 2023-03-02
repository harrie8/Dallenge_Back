package com.example.dailychallenge.entity.challenge;

import com.example.dailychallenge.exception.challenge.ChallengeCategoryNotFound;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public enum ChallengeCategory {
    STUDY(0, "공부"),
    VOLUNTEER(1, "봉사"),
    WORKOUT(2, "운동"),
    ECONOMY(3, "경제"),
    HEALTH(4, "건강"),
    ;

    private final int index;
    private final String description;

    ChallengeCategory(int index, String description) {
        this.index = index;
        this.description = description;
    }

    public static ChallengeCategory findByDescription(String description) {
        return Arrays.stream(values())
                .filter(challengeCategory -> challengeCategory.isSameDescription(description))
                .findAny()
                .orElseThrow(ChallengeCategoryNotFound::new);
    }

    public static ChallengeCategory findByIndex(int index) {
        return Arrays.stream(values())
                .filter(challengeCategory -> challengeCategory.isSameIndex(index))
                .findAny()
                .orElseThrow(ChallengeCategoryNotFound::new);    }

    private boolean isSameDescription(String description) {
        return this.description.equals(description);
    }

    private boolean isSameIndex(int index) {
        return this.index == index;
    }

    public static List<String> getDescriptions() {
        return Arrays.stream(values())
                .map(ChallengeCategory::getDescription)
                .collect(Collectors.toUnmodifiableList());
    }
}
