package com.example.dailychallenge.entity.badge.type;

import com.example.dailychallenge.dto.BadgeDto;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// 후기 N개 작성
public enum CommentWriteBadgeType implements BadgeType{

    WROTE_10(10, "comment10"),
    WROTE_15(15, "comment15"),
    WROTE_20(20, "comment20"),
    WROTE_25(25, "comment25"),
    WROTE_30(30, "comment30"),
    ;

    private final static String CREATE = "작성";
    private final static String DIRECTORY = "comment/";
    private final int number;
    private final String imgFileName;

    CommentWriteBadgeType(int number, String imgFileName) {
        this.number = number;
        this.imgFileName = imgFileName;
    }

    public static Optional<CommentWriteBadgeType> findByNumber(int number) {
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
        return String.format("후기 %d개 " + CREATE, this.number);
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
}
