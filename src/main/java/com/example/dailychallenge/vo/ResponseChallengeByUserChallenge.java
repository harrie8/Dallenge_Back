package com.example.dailychallenge.vo;

import com.example.dailychallenge.entity.challenge.ChallengeStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Getter
@NoArgsConstructor
public class ResponseChallengeByUserChallenge {
    private Long challengeId;
    private String challengeTitle;
    private String challengeContent;
    @Enumerated(value = EnumType.STRING)
    private ChallengeStatus challengeStatus;

    @Builder
    public ResponseChallengeByUserChallenge(Long challengeId, String challengeTitle,
                                            String challengeContent, ChallengeStatus challengeStatus) {
        this.challengeId = challengeId;
        this.challengeTitle = challengeTitle;
        this.challengeContent = challengeContent;
        this.challengeStatus = challengeStatus;
    }
}
