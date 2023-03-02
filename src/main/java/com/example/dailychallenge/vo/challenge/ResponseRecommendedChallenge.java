package com.example.dailychallenge.vo.challenge;

import com.example.dailychallenge.entity.challenge.Challenge;
import com.querydsl.core.annotations.QueryProjection;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResponseRecommendedChallenge {
    private Long id;
    private String title;
    private String content;
    private List<String> challengeImgUrls;

    @Builder
    public ResponseRecommendedChallenge(Long id, String title, String content, List<String> challengeImgUrls) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.challengeImgUrls = challengeImgUrls;
    }

    @QueryProjection
    public ResponseRecommendedChallenge(Challenge challenge) {
        this.id = challenge.getId();
        this.title = challenge.getTitle();
        this.content = challenge.getContent();
        this.challengeImgUrls = challenge.getImgUrls();
    }
}
