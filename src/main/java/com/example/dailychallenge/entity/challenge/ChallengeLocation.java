package com.example.dailychallenge.entity.challenge;

import com.example.dailychallenge.exception.challenge.ChallengeLocationNotFound;
import java.util.Arrays;
import lombok.Getter;

@Getter
public enum ChallengeLocation {
    INDOOR("실내"),
    OUTDOOR("실외"),
    ;

    private final String description;

    ChallengeLocation(String description) {
        this.description = description;
    }

    public static ChallengeLocation findByDescription(String description) {
        return Arrays.stream(values())
                .filter(challengeLocation -> challengeLocation.isSameDescription(description))
                .findAny()
                .orElseThrow(ChallengeLocationNotFound::new);
    }

    private boolean isSameDescription(String description) {
        return this.description.equals(description);
    }
}
