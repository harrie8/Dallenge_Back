package com.example.dailychallenge.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResponseUserChallenge {
    private Long userId;
    private Long challengeId;
    private String challengeTitle;
    private String challengeContent;

    @Builder
    public ResponseUserChallenge(Long userId, Long challengeId, String challengeTitle, String challengeContent) {
        this.userId = userId;
        this.challengeId = challengeId;
        this.challengeTitle = challengeTitle;
        this.challengeContent = challengeContent;
    }
}
