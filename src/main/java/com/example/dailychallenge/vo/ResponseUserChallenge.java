package com.example.dailychallenge.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResponseUserChallenge {
    private Long challengeId;
    private String challengeTitle;
    private String challengeContent;

    @Builder
    public ResponseUserChallenge(Long challengeId, String challengeTitle, String challengeContent) {
        this.challengeId = challengeId;
        this.challengeTitle = challengeTitle;
        this.challengeContent = challengeContent;
    }
}
