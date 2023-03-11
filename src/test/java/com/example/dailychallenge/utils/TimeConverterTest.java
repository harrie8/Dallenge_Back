package com.example.dailychallenge.utils;

import static com.example.dailychallenge.utils.SpecificCreatedAt.ONE_DAY_BEFORE;
import static com.example.dailychallenge.utils.SpecificCreatedAt.THREE_DAYS_BEFORE;
import static com.example.dailychallenge.utils.SpecificCreatedAt.TODAY;
import static com.example.dailychallenge.utils.SpecificCreatedAt.TWO_DAYS_BEFORE;
import static com.example.dailychallenge.utils.SpecificCreatedAt.YEAR_MONTH_DAY;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class TimeConverterTest {

    static Stream<Arguments> generateData() {
        return Stream.of(
                Arguments.of(0, TODAY),
                Arguments.of(1, ONE_DAY_BEFORE),
                Arguments.of(2, TWO_DAYS_BEFORE),
                Arguments.of(3, THREE_DAYS_BEFORE)
        );
    }

    @ParameterizedTest
    @MethodSource("generateData")
    @DisplayName("생성 시간 변환 테스트")
    void makeSpecificCreatedAtTest(long minusDays, SpecificCreatedAt expect) {
        LocalDateTime createdAt = LocalDateTime.now().minusDays(minusDays);

        TimeConverter timeConverter = new TimeConverter();
        String specificCreatedAt = timeConverter.makeSpecificCreatedAt(createdAt);

        assertEquals(expect.getDescription(), specificCreatedAt);
    }

    @Test
    @DisplayName("생성 시간 변환 테스트")
    void makeYearMonthDaySpecificCreatedAtTest() {
        LocalDateTime createdAt = LocalDateTime.now().minusDays(4);

        TimeConverter timeConverter = new TimeConverter();
        String specificCreatedAt = timeConverter.makeSpecificCreatedAt(createdAt);

        assertEquals(createdAt.format(DateTimeFormatter.ofPattern(YEAR_MONTH_DAY.getDescription())), specificCreatedAt);
    }
}