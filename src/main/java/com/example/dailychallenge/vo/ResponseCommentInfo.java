package com.example.dailychallenge.vo;

import com.example.dailychallenge.entity.comment.Comment;
import java.util.ArrayList;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResponseCommentInfo {
    private Long commentId;
    private String commentContent;
    private List<String> commentImgs;
    private String commentCreatedAt;

    @Builder
    public ResponseCommentInfo(Long commentId, String commentContent, List<String> commentImgs,
                               String commentCreatedAt) {
        this.commentId = commentId;
        this.commentContent = commentContent;
        this.commentImgs = commentImgs;
        this.commentCreatedAt = commentCreatedAt;
    }

    public static List<ResponseCommentInfo> convert(List<Comment> comments) {
        List<ResponseCommentInfo> result = new ArrayList<>();
        for (Comment comment : comments) {
            String commentContent = makeCommentContent(comment);
            ResponseCommentInfo responseCommentInfo = ResponseCommentInfo.builder()
                    .commentId(comment.getId())
                    .commentContent(commentContent)
                    .commentImgs(comment.getImgUrls())
                    .commentCreatedAt(comment.getMonthDayFormatCreatedAt())
                    .build();
            result.add(responseCommentInfo);
        }
        return result;
    }

    private static String makeCommentContent(Comment comment) {
        String commentContent = comment.getContent();
        if (commentContent == null) {
            commentContent = "";
        }
        return commentContent;
    }
}
