package com.example.dailychallenge.util.fixture.hashtag;

import com.example.dailychallenge.entity.hashtag.Hashtag;
import java.util.ArrayList;
import java.util.List;

public class HashtagFixture {

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

    public static List<Hashtag> createSpecificHashtags(List<String> hashtagDto) {
        List<Hashtag> hashtags = new ArrayList<>();
        for (String tag : hashtagDto) {
            Hashtag hashtag = Hashtag.builder()
                    .content(tag)
                    .build();
            hashtags.add(hashtag);
        }
        return hashtags;
    }
}
