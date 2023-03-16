package com.example.dailychallenge.entity.badge.type;

import com.example.dailychallenge.dto.BadgeDto;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// 챌린지 N개 달성
public enum AchievementBadgeType implements BadgeType{

    ACHIEVED_10(10, "achievement10"),
    ACHIEVED_20(20, "achievement20"),
    ACHIEVED_30(30, "achievement30"),
    ACHIEVED_40(40, "achievement40"),
    ACHIEVED_50(50, "achievement50"),
    ;

    private static final String ACHIEVEMENT = "달성";
    private final static String DIRECTORY = "achievement/";
    private final int number;
    private final String imgFileName;

    AchievementBadgeType(int number, String imgFileName) {
        this.number = number;
        this.imgFileName = imgFileName;
    }

    public static Optional<AchievementBadgeType> findByNumber(int number) {
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
        return String.format("챌린지 %d개 " + ACHIEVEMENT, this.number);
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
        return badgeName.contains(ACHIEVEMENT);
    }
}
