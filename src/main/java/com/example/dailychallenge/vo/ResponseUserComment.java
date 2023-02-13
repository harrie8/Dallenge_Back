package com.example.dailychallenge.vo;

import com.example.dailychallenge.entity.comment.Comment;
import com.querydsl.core.annotations.QueryProjection;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class ResponseUserComment {
    private Long id;
    private String content;
    private Integer likes;
    private String createdAt;
    private List<String> commentImgUrls;
    private Long challengeId;
    private String challengeTitle;

    @Builder
    public ResponseUserComment(Long id, String content, Integer likes, String createdAt,
                               List<String> commentImgUrls, Long challengeId, String challengeTitle) {

        this.id = id;
        this.content = content;
        this.likes = likes;
        this.createdAt = createdAt;
        this.commentImgUrls = commentImgUrls;
        this.challengeId = challengeId;
        this.challengeTitle = challengeTitle;
    }

    @QueryProjection
    public ResponseUserComment(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.likes = comment.getLikes();
        this.createdAt = comment.getFormattedCreatedAt();
        this.commentImgUrls = comment.getImgUrls();
        this.challengeId = comment.getChallenge().getId();
        this.challengeTitle = comment.getChallenge().getTitle();
    }
}
