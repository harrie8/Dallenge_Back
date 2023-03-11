package com.example.dailychallenge.utils;

import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;

public class TimeConverter {

    private int getBetweenDaysWithNow(LocalDateTime createdAt) {
        LocalDateTime now = LocalDateTime.now();

        return Period.between(createdAt.toLocalDate(), now.toLocalDate()).getDays();
    }

    public String makeSpecificCreatedAt(LocalDateTime createdAt) {
        int betweenDaysWithNow = getBetweenDaysWithNow(createdAt);

        SpecificCreatedAt specificCreatedAt = SpecificCreatedAt.findBy(betweenDaysWithNow);

        if (specificCreatedAt.isYearMonthDay()) {
            return createdAt.format(DateTimeFormatter.ofPattern(specificCreatedAt.getDescription()));
        }

        return specificCreatedAt.getDescription();
    }
}
