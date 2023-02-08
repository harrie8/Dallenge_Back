package com.example.dailychallenge.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class ChallengeEditor {

    private final String title;
    private final String content;
    private final String category;

    @Builder
    public ChallengeEditor(String title, String content, String category) {
        this.title = title;
        this.content = content;
        this.category = category;
    }
}
