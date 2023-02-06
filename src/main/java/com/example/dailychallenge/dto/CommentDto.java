package com.example.dailychallenge.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentDto {

    private String content;

    @Builder
    public CommentDto(String content) {
        this.content = content;
    }
}
