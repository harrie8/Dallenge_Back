package com.example.dailychallenge.vo.badge;

import com.example.dailychallenge.entity.badge.Badge;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.UserChallenge;
import com.example.dailychallenge.vo.ResponseUser;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseCreateChallengeBadge {

    private Long id;
    private String title;
    private String content;
    private String challengeCategory;
    private String challengeLocation;
    private String challengeDuration;
    private String challengeStatus;
    private List<String> challengeImgUrls;
    private List<String> challengeHashtags;
    private ResponseUser challengeOwnerUser;
    private ResponseCreateBadge badgeInfo;

    @Builder
    public ResponseCreateChallengeBadge(Long id, String title, String content, String challengeCategory,
                                        String challengeLocation, String challengeDuration, String challengeStatus,
                                        List<String> challengeImgUrls, List<String> challengeHashtags,
                                        ResponseUser responseUser, ResponseCreateBadge responseCreateBadge) {

        this.id = id;
        this.title = title;
        this.content = content;
        this.challengeCategory = challengeCategory;
        this.challengeLocation = challengeLocation;
        this.challengeDuration = challengeDuration;
        this.challengeStatus = challengeStatus;
        this.challengeImgUrls = challengeImgUrls;
        this.challengeHashtags = challengeHashtags;
        this.challengeOwnerUser = responseUser;
        this.badgeInfo = responseCreateBadge;
    }

    public static ResponseCreateChallengeBadge create(Challenge challenge, UserChallenge userChallenge,
                                                      Badge badge) {
        ResponseUser responseUser = ResponseUser.create(challenge.getUsers());
        ResponseCreateBadge responseCreateBadge = ResponseCreateBadge.create(badge);

        return ResponseCreateChallengeBadge.builder()
                .id(challenge.getId())
                .title(challenge.getTitle())
                .content(challenge.getContent())
                .challengeCategory(challenge.getChallengeCategory().getDescription())
                .challengeLocation(challenge.getChallengeLocation().getDescription())
                .challengeDuration(challenge.getChallengeDuration().getDescription())
                .challengeStatus(userChallenge.getChallengeStatus().getDescription())
                .challengeImgUrls(challenge.getImgUrls())
                .challengeHashtags(challenge.getHashtags())
                .responseUser(responseUser)
                .responseCreateBadge(responseCreateBadge)
                .build();
    }
}
