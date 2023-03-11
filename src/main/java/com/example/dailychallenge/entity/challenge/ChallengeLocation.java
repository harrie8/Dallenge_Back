package com.example.dailychallenge.entity.challenge;

import com.example.dailychallenge.exception.challenge.ChallengeLocationNotFound;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public enum ChallengeLocation {
    OUTDOOR(0, "실외"),
    INDOOR(1, "실내"),
    ;

    private final int index;
    private final String description;

    ChallengeLocation(int index, String description) {
        this.index = index;
        this.description = description;
    }

    public static ChallengeLocation findByDescription(String description) {
        return Arrays.stream(values())
                .filter(challengeLocation -> challengeLocation.isSameDescription(description))
                .findAny()
                .orElseThrow(ChallengeLocationNotFound::new);
    }

    public static ChallengeLocation findByIndex(int index) {
        return Arrays.stream(values())
                .filter(challengeLocation -> challengeLocation.isSameIndex(index))
                .findAny()
                .orElseThrow(ChallengeLocationNotFound::new);
    }

    private boolean isSameDescription(String description) {
        return this.description.equals(description);
    }

    private boolean isSameIndex(int index) {
        return this.index == index;
    }

    public static List<String> getDescriptions() {
        return Arrays.stream(values())
                .map(ChallengeLocation::getDescription)
                .collect(Collectors.toUnmodifiableList());
    }
}
