package com.example.dailychallenge.entity.badge.type;

import java.util.Arrays;
import java.util.Optional;

// 챌린지 N개 생성
public enum ChallengeCreatetBadgeType implements BadgeType{

    CREATED_10(10),
    CREATED_15(15),
    CREATED_20(200),
    CREATED_25(25),
    CREATED_30(300),
    ;

    private final int number;

    ChallengeCreatetBadgeType(int number) {
        this.number = number;
    }

    public static Optional<ChallengeCreatetBadgeType> findByNumber(int number) {
        return Arrays.stream(values())
                .filter(badgeType -> badgeType.isSameNumber(number))
                .findAny();
    }

    @Override
    public boolean isSameNumber(int number) {
        return this.number == number;
    }

    @Override
    public String getName() {
        return String.format("챌린지 %d개 생성", this.number);
    }
}
