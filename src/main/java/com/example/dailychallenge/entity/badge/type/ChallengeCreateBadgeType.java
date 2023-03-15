package com.example.dailychallenge.entity.badge.type;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// 챌린지 N개 생성
public enum ChallengeCreateBadgeType implements BadgeType{

    CREATED_10(10),
    CREATED_15(15),
    CREATED_20(20),
    CREATED_25(25),
    CREATED_30(30),
    ;

    private final static String CREATE = "생성";
    private final int number;

    ChallengeCreateBadgeType(int number) {
        this.number = number;
    }

    public static Optional<ChallengeCreateBadgeType> findByNumber(int number) {
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
        return String.format("챌린지 %d개 " + CREATE, this.number);
    }

    public static List<String> getNames() {
        return Arrays.stream(values())
                .map(ChallengeCreateBadgeType::getName)
                .collect(Collectors.toUnmodifiableList());
    }

    public static boolean isSameType(String badgeName) {
        return badgeName.contains(CREATE);
    }
}
