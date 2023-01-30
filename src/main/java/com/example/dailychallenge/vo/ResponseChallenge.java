package com.example.dailychallenge.vo;

import com.example.dailychallenge.entity.challenge.Challenge;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseChallenge {
    private String title;
    private String content;
    private String challengeCategory;
    private String challengeLocation;
    private String challengeDuration;
    private Long howManyUsersAreInThisChallenge;

    @Builder
    public ResponseChallenge(String title, String content, String challengeCategory, String challengeLocation,
                             String challengeDuration, Long howManyUsersAreInThisChallenge) {
        this.title = title;
        this.content = content;
        this.challengeCategory = challengeCategory;
        this.challengeLocation = challengeLocation;
        this.challengeDuration = challengeDuration;
        this.howManyUsersAreInThisChallenge = howManyUsersAreInThisChallenge;
    }

    @QueryProjection
    public ResponseChallenge(Challenge challenge, Long howManyUsersAreInThisChallenge) {
        this.title = challenge.getTitle();
        this.content = challenge.getContent();
        this.challengeCategory = challenge.getChallengeCategory().getDescription();
        this.challengeLocation = challenge.getChallengeLocation().getDescription();
        this.challengeDuration = challenge.getChallengeDuration().getDescription();
        this.howManyUsersAreInThisChallenge = howManyUsersAreInThisChallenge;
    }
}
