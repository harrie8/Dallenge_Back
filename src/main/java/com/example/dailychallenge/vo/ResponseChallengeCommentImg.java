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
public class ResponseChallengeCommentImg {
    private Long id;
    private List<String> commentImgUrls;

    @Builder
    public ResponseChallengeCommentImg(Long id, List<String> commentImgUrls) {
        this.id = id;
        this.commentImgUrls = commentImgUrls;
    }

    @QueryProjection
    public ResponseChallengeCommentImg(Comment comment) {
        this.id = comment.getId();
        this.commentImgUrls = comment.getImgUrls();
    }
}
