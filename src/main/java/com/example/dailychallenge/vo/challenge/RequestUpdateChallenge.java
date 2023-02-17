package com.example.dailychallenge.vo.challenge;

import com.example.dailychallenge.exception.CommonException;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RequestUpdateChallenge {
    @NotBlank(message = "제목을 입력해주세요.")
    private String title;
    @NotBlank(message = "내용을 입력해주세요.")
    private String content;
    @NotBlank(message = "챌린지 카테고리를 입력해주세요.")
    private String challengeCategory;

    @Builder
    public RequestUpdateChallenge(String title, String content, String challengeCategory) {
        validate(title, content, challengeCategory);
        this.title = title;
        this.content = content;
        this.challengeCategory = challengeCategory;
    }

    private void validate(String title, String content, String challengeCategory) {
        if (title == null || title.isEmpty()) {
            throw new CommonException("제목을 입력해주세요.");
        }
        if (content == null || content.isEmpty()) {
            throw new CommonException("내용을 입력해주세요.");
        }
        if (challengeCategory == null || challengeCategory.isEmpty()) {
            throw new CommonException("카테고리를 입력해주세요.");
        }
    }
}
