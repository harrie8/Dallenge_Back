package com.example.dailychallenge.utils;

import java.util.Arrays;

public enum SpecificCreatedAt {

    TODAY(0, "오늘"),
    ONE_DAY_BEFORE(1, "1일전"),
    TWO_DAYS_BEFORE(2, "2일전"),
    THREE_DAYS_BEFORE(3, "3일전"),
    YEAR_MONTH_DAY(4, "yyyy-MM-dd"),
    ;

    private final int betweenDays;
    private final String description;

    SpecificCreatedAt(int betweenDays, String description) {
        this.betweenDays = betweenDays;
        this.description = description;
    }

    public static SpecificCreatedAt findBy(int betweenDays) {
        if (betweenDays >= YEAR_MONTH_DAY.betweenDays) {
            return YEAR_MONTH_DAY;
        }

        return Arrays.stream(values())
                .filter(createdTimeFormat -> createdTimeFormat.betweenDays == betweenDays)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("특정 생성 날짜를 찾을 수 없습니다."));
    }

    public boolean isYearMonthDay() {
        return this == YEAR_MONTH_DAY;
    }

    public String getDescription() {
        return description;
    }
}
