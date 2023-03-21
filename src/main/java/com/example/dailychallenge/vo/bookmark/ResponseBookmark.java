package com.example.dailychallenge.vo.bookmark;

import com.example.dailychallenge.entity.bookmark.Bookmark;
import com.querydsl.core.annotations.QueryProjection;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResponseBookmark {
    private Long id;
    private String title;
    private String createdAt;
    private Long userId;
    private Long challengeId;
    private String challengeContent;
    private List<String> challengeImgUrls;

    @Builder
    public ResponseBookmark(Long id, String title, String createdAt, Long userId, Long challengeId,
                            String challengeContent, List<String> challengeImgUrls) {
        this.id = id;
        this.title = title;
        this.createdAt = createdAt;
        this.userId = userId;
        this.challengeId = challengeId;
        this.challengeContent = challengeContent;
        this.challengeImgUrls = challengeImgUrls;
    }

    @QueryProjection
    public ResponseBookmark(Bookmark bookmark) {
        this.id = bookmark.getId();
        this.title = bookmark.getChallenge().getTitle();
        this.createdAt = bookmark.getFormattedCreatedAt();
        this.userId = bookmark.getUsers().getId();
        this.challengeId = bookmark.getChallenge().getId();
        this.challengeContent = bookmark.getChallenge().getContent();
        this.challengeImgUrls = bookmark.getChallenge().getImgUrls();
    }
}
