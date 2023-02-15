package com.example.dailychallenge.vo.bookmark;

import com.example.dailychallenge.entity.bookmark.Bookmark;
import com.querydsl.core.annotations.QueryProjection;
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

    @QueryProjection
    public ResponseBookmark(Bookmark bookmark) {
        this.title = bookmark.getChallenge().getTitle();
        this.createdAt = bookmark.getFormattedCreatedAt();
        this.userId = bookmark.getUsers().getId();
    }
}
