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
    private ResponseCreateBadge badgeInfo;

    @Builder
    public ResponseCommentWriteBadge(Long id, String content, String createdAt, Long userId, ResponseCreateBadge responseCreateBadge) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.userId = userId;
        this.badgeInfo = responseCreateBadge;
    }
}
