package com.example.dailychallenge.vo.bookmark;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResponseBookmark {
    private String title;
    private String createdAt;
    private Long userId;


    @Builder
    public ResponseBookmark(String title, String createdAt, Long userId) {
        this.title = title;
        this.createdAt = createdAt;
        this.userId = userId;
    }
}
