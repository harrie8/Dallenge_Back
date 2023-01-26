package com.example.dailychallenge.vo;

import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
public class RequestCreateChallenge {
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    @NotBlank
    private String challengeCategory;
    @NotBlank
    private String challengeLocation;
    @NotBlank
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
