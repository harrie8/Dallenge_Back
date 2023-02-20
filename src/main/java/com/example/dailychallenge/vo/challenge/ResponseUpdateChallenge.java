package com.example.dailychallenge.vo.challenge;

import com.example.dailychallenge.entity.challenge.Challenge;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseUpdateChallenge {
    private Long id;
    private String title;
    private String content;
    private String challengeCategory;
    private String challengeLocation;
    private String challengeDuration;
    private String created_at;
    private String updated_at;
    private List<String> challengeImgUrls;
    private List<String> challengeHashtags;

    @Builder
    public ResponseUpdateChallenge(Long id, String title, String content, String challengeCategory,
                                   String challengeLocation,
                                   String challengeDuration, String created_at, String updated_at,
                                   List<String> challengeImgUrls, List<String> challengeHashtags) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.challengeCategory = challengeCategory;
        this.challengeLocation = challengeLocation;
        this.challengeDuration = challengeDuration;
        this.created_at = created_at;
        this.updated_at = updated_at;
        this.challengeImgUrls = challengeImgUrls;
        this.challengeHashtags = challengeHashtags;
    }

    public static ResponseUpdateChallenge create(Challenge updatedChallenge) {
        return ResponseUpdateChallenge.builder()
                .id(updatedChallenge.getId())
                .title(updatedChallenge.getTitle())
                .content(updatedChallenge.getContent())
                .challengeCategory(updatedChallenge.getChallengeCategory().getDescription())
                .challengeLocation(updatedChallenge.getChallengeLocation().getDescription())
                .challengeDuration(updatedChallenge.getChallengeDuration().getDescription())
                .created_at(updatedChallenge.getFormattedCreatedAt())
                .updated_at(updatedChallenge.getFormattedUpdatedAt())
                .challengeImgUrls(updatedChallenge.getImgUrls())
                .challengeHashtags(updatedChallenge.getHashtags())
                .build();
    }
}
