package com.example.dailychallenge.entity.badge.type;

import com.example.dailychallenge.dto.BadgeDto;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// 챌린지 N개 생성
public enum ChallengeCreateBadgeType implements BadgeType{

    CREATED_10(10,"challengeCreate10"),
    CREATED_15(15, "challengeCreate15"),
    CREATED_20(20, "challengeCreate20"),
    CREATED_25(25, "challengeCreate25"),
    CREATED_30(30, "challengeCreate30"),
    ;

    private final static String CREATE = "생성";
    private final static String DIRECTORY = "challengeCreate/";
    private final int number;
    private final String imgFileName;

    ChallengeCreateBadgeType(int number, String imgFileName) {
        this.number = number;
        this.imgFileName = imgFileName;
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

    public String getImgFileName() {
        return this.imgFileName;
    }

    public static List<BadgeDto> getBadgeDtos() {
        return Arrays.stream(values())
                .map(challengeCreateBadgeType -> BadgeDto.builder()
                        .badgeName(challengeCreateBadgeType.getName())
                        .badgeImgFileName(DIRECTORY + challengeCreateBadgeType.getImgFileName())
                        .build())
                .collect(Collectors.toUnmodifiableList());
    }

    public static boolean isSameType(String badgeName) {
        return badgeName.contains(CREATE);
    }
}
