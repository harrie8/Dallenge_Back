package com.example.dailychallenge.vo;

import com.example.dailychallenge.entity.challenge.ChallengeStatus;
import java.time.LocalDateTime;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ResponseChallengeByUserChallenge {
    private Long userId;
    private Long challengeId;
    private String challengeTitle;
    private String challengeContent;
    @Enumerated(value = EnumType.STRING)
    private ChallengeStatus challengeStatus;
    private LocalDateTime createdAt;

    @Builder
    public ResponseChallengeByUserChallenge(Long userId, Long challengeId, String challengeTitle,
                                            String challengeContent, ChallengeStatus challengeStatus,
                                            LocalDateTime createdAt) {

        this.userId = userId;
        this.challengeId = challengeId;
        this.challengeTitle = challengeTitle;
        this.challengeContent = challengeContent;
        this.challengeStatus = challengeStatus;
        this.createdAt = createdAt;
    }
}
