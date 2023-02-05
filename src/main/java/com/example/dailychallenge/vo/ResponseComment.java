package com.example.dailychallenge.vo;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ResponseComment {
    private String content;
    private LocalDateTime createdAt;
    private Long userId;


    @Builder
    public ResponseComment(String content, LocalDateTime createdAt, Long userId) {
        this.content = content;
        this.createdAt = createdAt;
        this.userId = userId;
    }
}
