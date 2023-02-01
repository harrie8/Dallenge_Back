package com.example.dailychallenge.vo;

import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.UserChallenge;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseCreateChallenge {

    private Long id;
    private String title;
    private String content;
    private String challengeCategory;
    private String challengeLocation;
    private String challengeDuration;
    private String challengeStatus;
    private String challengeImgUrl;
    private ResponseUser challengeOwnerUser;

    @Builder
    public ResponseCreateChallenge(Long id, String title, String content, String challengeCategory,
                                   String challengeLocation,
                                   String challengeDuration, String challengeStatus, String challengeImgUrl,
                                   ResponseUser responseUser) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.challengeCategory = challengeCategory;
        this.challengeLocation = challengeLocation;
        this.challengeDuration = challengeDuration;
        this.challengeStatus = challengeStatus;
        this.challengeImgUrl = challengeImgUrl;
        this.challengeOwnerUser = responseUser;
    }

    public static ResponseCreateChallenge create(Challenge challenge, UserChallenge userChallenge) {
        ResponseUser responseUser = ResponseUser.create(challenge.getUsers());
        String challengeImgUrl = null;
        if (challenge.getChallengeImg() != null) {
            challengeImgUrl = challenge.getChallengeImg().getImgUrl();
        }

        return ResponseCreateChallenge.builder()
                .id(challenge.getId())
                .title(challenge.getTitle())
                .content(challenge.getContent())
                .challengeCategory(challenge.getChallengeCategory().getDescription())
                .challengeLocation(challenge.getChallengeLocation().getDescription())
                .challengeDuration(challenge.getChallengeDuration().getDescription())
                .challengeStatus(userChallenge.getChallengeStatus().getDescription())
                .challengeImgUrl(challengeImgUrl)
                .responseUser(responseUser)
                .build();
    }
}
