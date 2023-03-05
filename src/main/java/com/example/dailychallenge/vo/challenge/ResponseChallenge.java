package com.example.dailychallenge.vo.challenge;

import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.vo.ResponseUser;
import com.querydsl.core.annotations.QueryProjection;
import java.util.List;
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
    private String created_at;
    private List<String> challengeImgUrls;
    private List<String> challengeHashtags;
    private Long howManyUsersAreInThisChallenge;
    private ResponseUser challengeOwnerUser;

    @Builder
    public ResponseChallenge(Long id, String title, String content, String challengeCategory, String challengeLocation,
                             String challengeDuration, String created_at, List<String> challengeImgUrls,
                             List<String> challengeHashtags, Long howManyUsersAreInThisChallenge,
                             ResponseUser responseUser) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.challengeCategory = challengeCategory;
        this.challengeLocation = challengeLocation;
        this.challengeDuration = challengeDuration;
        this.created_at = created_at;
        this.challengeImgUrls = challengeImgUrls;
        this.challengeHashtags = challengeHashtags;
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
        this.created_at = challenge.getFormattedCreatedAt();
        this.challengeImgUrls = challenge.getImgUrls();
        this.challengeHashtags = challenge.getHashtags();
        this.howManyUsersAreInThisChallenge = howManyUsersAreInThisChallenge;
        this.challengeOwnerUser = ResponseUser.create(challenge.getUsers());
    }

    @QueryProjection
    public ResponseChallenge(Challenge challenge) {
        this.id = challenge.getId();
        this.title = challenge.getTitle();
        this.content = challenge.getContent();
        this.challengeCategory = challenge.getChallengeCategory().getDescription();
        this.challengeLocation = challenge.getChallengeLocation().getDescription();
        this.challengeDuration = challenge.getChallengeDuration().getDescription();
        this.created_at = challenge.getFormattedCreatedAt();
        this.challengeImgUrls = challenge.getImgUrls();
        this.challengeHashtags = challenge.getHashtags();
        this.challengeOwnerUser = ResponseUser.create(challenge.getUsers());
    }
}
