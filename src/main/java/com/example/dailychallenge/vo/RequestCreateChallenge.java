package com.example.dailychallenge.vo;

import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
public class RequestCreateChallenge {
    @NotBlank(message = "제목을 입력해주세요.")
    private String title;
    @NotBlank(message = "내용을 입력해주세요.")
    private String content;
    @NotBlank(message = "챌린지 카테고리를 입력해주세요.")
    private String challengeCategory;
    @NotBlank(message = "챌린지 장소를 입력해주세요.")
    private String challengeLocation;
    @NotBlank(message = "챌린지 기간을 입력해주세요.")
    private String challengeDuration;

    @Builder
    public RequestCreateChallenge(String title, String content, String challengeCategory, String challengeLocation,
                                  String challengeDuration) {
        this.title = title;
        this.content = content;
        this.challengeCategory = challengeCategory;
        this.challengeLocation = challengeLocation;
        this.challengeDuration = challengeDuration;
    }
}
