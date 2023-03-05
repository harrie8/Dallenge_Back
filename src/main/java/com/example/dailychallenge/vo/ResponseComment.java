package com.example.dailychallenge.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResponseComment {
    private Long id;
    private String content;
    private String createdAt;
    private Long userId;


    @Builder
    public ResponseComment(Long id, String content, String createdAt, Long userId) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.userId = userId;
    }
}
