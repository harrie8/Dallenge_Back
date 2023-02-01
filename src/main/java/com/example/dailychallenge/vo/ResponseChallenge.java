package com.example.dailychallenge.vo;

import com.example.dailychallenge.entity.challenge.Challenge;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseChallenge {
    private Long id;
    private String title;
    private String content;
    private String challengeCategory;
    private String challengeLocation;
    private String challengeDuration;
    private String challengeImgUrl;
    private Long howManyUsersAreInThisChallenge;
    private ResponseUser challengeOwnerUser;

    @Builder
    public ResponseChallenge(Long id, String title, String content, String challengeCategory, String challengeLocation,
                             String challengeDuration, String challengeImgUrl, Long howManyUsersAreInThisChallenge, ResponseUser responseUser) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.challengeCategory = challengeCategory;
        this.challengeLocation = challengeLocation;
        this.challengeDuration = challengeDuration;
        this.challengeImgUrl = challengeImgUrl;
        this.howManyUsersAreInThisChallenge = howManyUsersAreInThisChallenge;
        this.challengeOwnerUser = responseUser;
    }

    @QueryProjection
    public ResponseChallenge(Challenge challenge, Long howManyUsersAreInThisChallenge) {
        this.id = challenge.getId();
        this.title = challenge.getTitle();
        this.content = challenge.getContent();
        this.challengeCategory = challenge.getChallengeCategory().getDescription();
        this.challengeLocation = challenge.getChallengeLocation().getDescription();
        this.challengeDuration = challenge.getChallengeDuration().getDescription();

        if (challenge.getChallengeImg() != null) {
            this.challengeImgUrl = challenge.getChallengeImg().getImgUrl();
        }
        if (challenge.getChallengeImg() == null) {
            this.challengeImgUrl = null;
        }

        this.howManyUsersAreInThisChallenge = howManyUsersAreInThisChallenge;
        this.challengeOwnerUser = ResponseUser.create(challenge.getUsers());
    }
}
