package com.example.dailychallenge.entity.challenge;

import com.example.dailychallenge.exception.challenge.ChallengeDurationNotFound;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public enum ChallengeDuration {
    WITHIN_TEN_MINUTES(0, "10분 이내"),
    WITHIN_TEN_TO_THIRTY_MINUTES(1, "10분 ~ 30분 이내"),
    WITHIN_THIRTY_MINUTES_TO_ONE_HOUR(2, "30분 ~ 1시간 이내"),
    OVER_ONE_HOUR(3, "1시간 이상"),
    ;

    private final int index;
    private final String description;

    ChallengeDuration(int index, String description) {
        this.index = index;
        this.description = description;
    }

    public static ChallengeDuration findByDescription(String description) {
        return Arrays.stream(values())
                .filter(challengeDuration -> challengeDuration.isSameDescription(description))
                .findAny()
                .orElseThrow(ChallengeDurationNotFound::new);
    }

    public static ChallengeDuration findByIndex(int index) {
        return Arrays.stream(values())
                .filter(challengeDuration -> challengeDuration.isSameIndex(index))
                .findAny()
                .orElseThrow(ChallengeDurationNotFound::new);
    }

    private boolean isSameDescription(String description) {
        return this.description.equals(description);
    }

    private boolean isSameIndex(int index) {
        return this.index == index;
    }

    public static List<String> getDescriptions() {
        return Arrays.stream(values())
                .map(ChallengeDuration::getDescription)
                .collect(Collectors.toUnmodifiableList());
    }
}
