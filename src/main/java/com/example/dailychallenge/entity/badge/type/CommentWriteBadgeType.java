package com.example.dailychallenge.entity.badge.type;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// 후기 N개 작성
public enum CommentWriteBadgeType implements BadgeType{

    WROTE_10(10),
    WROTE_15(15),
    WROTE_20(20),
    WROTE_25(25),
    WROTE_30(30),
    ;

    private final static String CREATE = "작성";
    private final int number;

    CommentWriteBadgeType(int number) {
        this.number = number;
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

    public static List<String> getNames() {
        return Arrays.stream(values())
                .map(CommentWriteBadgeType::getName)
                .collect(Collectors.toUnmodifiableList());
    }

    public static boolean isSameType(String badgeName) {
        return badgeName.contains(CREATE);
    }
}
