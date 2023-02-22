package com.example.dailychallenge.dto;

import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CommentDto {

    @NotBlank(message = "댓글의 내용은 비어서는 안 됩니다.")
    private String content;

    @Builder
    public CommentDto(String content) {
        this.content = content;
    }
}
