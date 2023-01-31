package com.example.dailychallenge.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChallengeSearchCondition {

    private String title;
    private String category;

    @Builder
    public ChallengeSearchCondition(String title, String category) {
        this.title = title;
        this.category = category;
    }
}
