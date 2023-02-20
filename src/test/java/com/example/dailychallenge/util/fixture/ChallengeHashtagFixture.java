package com.example.dailychallenge.util.fixture;

import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.hashtag.ChallengeHashtag;
import com.example.dailychallenge.entity.hashtag.Hashtag;
import java.util.ArrayList;
import java.util.List;

public class ChallengeHashtagFixture {

//    public static ChallengeDto createChallengeDto() {
//        return ChallengeDto.builder()
//                .title("제목입니다.")
//                .content("내용입니다.")
//                .challengeCategory(ChallengeCategory.STUDY.getDescription())
//                .challengeLocation(ChallengeLocation.INDOOR.getDescription())
//                .challengeDuration(ChallengeDuration.WITHIN_TEN_MINUTES.getDescription())
//                .build();
//    }
//
//    public static Challenge createChallenge() {
//        return Challenge.builder()
//                .title("제목입니다.")
//                .content("내용입니다.")
//                .challengeCategory(ChallengeCategory.STUDY)
//                .challengeLocation(ChallengeLocation.INDOOR)
//                .challengeDuration(ChallengeDuration.WITHIN_TEN_MINUTES)
//                .build();
//    }

    public static List<ChallengeHashtag> createSpecificChallengeHashtags(List<Hashtag> hashtags, Challenge challenge) {
        List<ChallengeHashtag> challengeHashtags = new ArrayList<>();
        for (Hashtag hashtag : hashtags) {
            ChallengeHashtag challengeHashtag = ChallengeHashtag.builder()
                    .hashtag(hashtag)
                    .challenge(challenge)
                    .build();

            challenge.getChallengeHashtags().add(challengeHashtag);
            hashtag.getChallengeHashtags().add(challengeHashtag);
            challengeHashtags.add(challengeHashtag);
        }
        return challengeHashtags;
    }
}
