package com.example.dailychallenge.util.fixture.userChallenge;

import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.ChallengeStatus;
import com.example.dailychallenge.entity.challenge.UserChallenge;
import com.example.dailychallenge.entity.users.User;

public class UserChallengeFixture {

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

    public static UserChallenge createSpecificUserChallenge(ChallengeStatus challengeStatus, User user,
                                                            Challenge challenge) {

        return UserChallenge.builder()
                .challengeStatus(challengeStatus)
                .users(user)
                .challenge(challenge)
                .build();
    }
}
