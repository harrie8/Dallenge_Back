package com.example.dailychallenge.vo.badge;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseCommentWriteBadge {

    private Long id;
    private String content;
    private String createdAt;
    private Long userId;
    private String createBadgeName;

    @Builder
    public ResponseCommentWriteBadge(Long id, String content, String createdAt, Long userId, String createBadgeName) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.userId = userId;
        this.createBadgeName = createBadgeName;
    }
}
