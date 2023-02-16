package com.example.dailychallenge.dto;

import com.example.dailychallenge.exception.comment.CommentDtoNotValid;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;

@Getter
public class CommentDto {

    private String content;
    private List<MultipartFile> commentDtoImg;

    private CommentDto() {
    }

    @Builder
    public CommentDto(String content, List<MultipartFile> commentDtoImg) {
        isValid(content, commentDtoImg);
        isContentNotBlank(content);
        this.content = content;
        this.commentDtoImg = commentDtoImg;
    }

    private void isValid(String content, List<MultipartFile> commentDtoImg) {
        if (content == null && commentDtoImg == null) {
            throw new CommentDtoNotValid();
        }
    }

    private void isContentNotBlank(String content) {
        if (content != null && content.isBlank()) {
            throw new CommentDtoNotValid("댓글 내용은 비어서는 안 됩니다.");
        }
    }

    public boolean isContentValid() {
        return content != null;
    }

    public boolean isCommentDtoImgValid() {
        return commentDtoImg != null;
    }
}
