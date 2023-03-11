package com.example.dailychallenge.vo.hashtag;

import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.hashtag.Hashtag;
import com.example.dailychallenge.vo.challenge.ResponseRecommendedChallenge;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResponseChallengeHashtag {

    private Long hashtagId;
    private String hashtagContent;
    private Integer hashtagTagCount;
    private List<ResponseRecommendedChallenge> recommendedChallenges;

    @Builder
    public ResponseChallengeHashtag(Long hashtagId, String hashtagContent, Integer hashtagTagCount,
                                    List<ResponseRecommendedChallenge> recommendedChallenges) {
        this.hashtagId = hashtagId;
        this.hashtagContent = hashtagContent;
        this.hashtagTagCount = hashtagTagCount;
        this.recommendedChallenges = recommendedChallenges;
    }

    public static ResponseChallengeHashtag create(Hashtag hashtag, List<Challenge> challenges) {
        List<ResponseRecommendedChallenge> recommendedChallenges = new ArrayList<>();
        for (Challenge challenge : challenges) {
            recommendedChallenges.add(ResponseRecommendedChallenge.create(challenge));
        }

        return ResponseChallengeHashtag.builder()
                .hashtagId(hashtag.getId())
                .hashtagContent(hashtag.getContent())
                .hashtagTagCount(hashtag.getTagCount())
                .recommendedChallenges(recommendedChallenges)
                .build();
    }
}
