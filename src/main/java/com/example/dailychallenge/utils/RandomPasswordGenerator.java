package com.example.dailychallenge.utils;

import java.util.UUID;

public class RandomPasswordGenerator {
    private static final int START_INDEX = 0;
    private static final String HYPHEN = "-";
    private static final String SPACE = "";

    public static String generate(int length) {
        return UUID.randomUUID().toString().toUpperCase().replaceAll(HYPHEN, SPACE)
                .substring(START_INDEX, length);
    }
}
