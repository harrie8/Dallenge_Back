package com.example.dailychallenge.util.fixture;

import com.example.dailychallenge.dto.ChallengeDto;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.ChallengeCategory;
import com.example.dailychallenge.entity.challenge.ChallengeDuration;
import com.example.dailychallenge.entity.challenge.ChallengeLocation;
import com.example.dailychallenge.entity.users.User;

public class ChallengeFixture {

    public static ChallengeDto createChallengeDto() {
        return ChallengeDto.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .challengeCategory(ChallengeCategory.STUDY.getDescription())
                .challengeLocation(ChallengeLocation.INDOOR.getDescription())
                .challengeDuration(ChallengeDuration.WITHIN_TEN_MINUTES.getDescription())
                .build();
    }

    public static Challenge createChallenge() {
        return Challenge.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .challengeCategory(ChallengeCategory.STUDY)
                .challengeLocation(ChallengeLocation.INDOOR)
                .challengeDuration(ChallengeDuration.WITHIN_TEN_MINUTES)
                .build();
    }

    public static Challenge createSpecificChallenge(String title, String content, ChallengeCategory challengeCategory,
                                                    ChallengeLocation challengeLocation,
                                                    ChallengeDuration challengeDuration, User user) {
        Challenge challenge = Challenge.builder()
                .title(title)
                .content(content)
                .challengeCategory(challengeCategory)
                .challengeLocation(challengeLocation)
                .challengeDuration(challengeDuration)
                .build();
        challenge.setUser(user);

        return challenge;
    }
}
