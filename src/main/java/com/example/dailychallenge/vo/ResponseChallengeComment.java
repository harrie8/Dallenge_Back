package com.example.dailychallenge.vo;

import com.example.dailychallenge.entity.comment.Comment;
import com.querydsl.core.annotations.QueryProjection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class ResponseChallengeComment {
    private Long id;
    private String content;
    private Integer likes;
    private String createdAt;
    private List<String> commentImgUrls;
    private ResponseUser commentOwnerUser;
    private List<ResponseUser> commentLikeUsersInfo;

    @Builder
    public ResponseChallengeComment(Long id, String content, Integer likes, String createdAt,
                                    List<String> commentImgUrls, ResponseUser responseUser,
                                    List<ResponseUser> commentLikeUsersInfo) {

        this.id = id;
        this.content = content;
        this.likes = likes;
        this.createdAt = createdAt;
        this.commentImgUrls = commentImgUrls;
        this.commentOwnerUser = responseUser;
        this.commentLikeUsersInfo = commentLikeUsersInfo;
    }

    @QueryProjection
    public ResponseChallengeComment(Comment comment) {
        this.id = comment.getId();
        this.content = comment.getContent();
        this.likes = comment.getHearts().size();
        this.createdAt = comment.getSpecificCreatedAt();
        this.commentImgUrls = comment.getImgUrls();
        this.commentOwnerUser = ResponseUser.create(comment.getUsers());
        this.commentLikeUsersInfo = comment.getHearts().stream()
                .map(heart -> ResponseUser.create(heart.getUsers()))
                .collect(Collectors.toUnmodifiableList());
    }
}
