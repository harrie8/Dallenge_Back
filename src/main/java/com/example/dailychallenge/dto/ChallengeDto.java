package com.example.dailychallenge.dto;

import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.ChallengeCategory;
import com.example.dailychallenge.entity.challenge.ChallengeDuration;
import com.example.dailychallenge.entity.challenge.ChallengeLocation;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChallengeDto {
    private String title;
    private String content;
    private String challengeCategory;
    private String challengeLocation;
    private String challengeDuration;

    @Builder
    public ChallengeDto(String title, String content, String challengeCategory, String challengeLocation,
                        String challengeDuration) {
        this.title = title;
        this.content = content;
        this.challengeCategory = challengeCategory;
        this.challengeLocation = challengeLocation;
        this.challengeDuration = challengeDuration;
    }

    public Challenge toChallenge() {
        return Challenge.builder()
                .title(title)
                .content(content)
                .challengeCategory(ChallengeCategory.findByDescription(challengeCategory))
                .challengeLocation(ChallengeLocation.findByDescription(challengeLocation))
                .challengeDuration(ChallengeDuration.findByDescription(challengeDuration))
                .build();
    }
}
