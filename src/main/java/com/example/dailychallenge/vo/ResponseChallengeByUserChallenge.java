package com.example.dailychallenge.vo;

import com.example.dailychallenge.entity.challenge.ChallengeStatus;
import com.example.dailychallenge.entity.comment.Comment;
import java.time.LocalDateTime;
import java.util.List;
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
    private List<ResponseCommentInfo> comments;

    @Builder
    public ResponseChallengeByUserChallenge(Long userId, Long challengeId, String challengeTitle,
                                            String challengeContent, ChallengeStatus challengeStatus,
                                            LocalDateTime createdAt, List<Comment> comments) {

        this.userId = userId;
        this.challengeId = challengeId;
        this.challengeTitle = challengeTitle;
        this.challengeContent = challengeContent;
        this.challengeStatus = challengeStatus;
        this.createdAt = createdAt;
        if (comments != null) {
            this.comments = ResponseCommentInfo.convert(comments);
        }
    }
}
