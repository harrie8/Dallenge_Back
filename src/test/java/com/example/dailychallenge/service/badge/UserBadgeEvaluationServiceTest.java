package com.example.dailychallenge.service.badge;

import static com.example.dailychallenge.util.fixture.TokenFixture.EMAIL;
import static com.example.dailychallenge.util.fixture.TokenFixture.PASSWORD;
import static com.example.dailychallenge.util.fixture.challenge.ChallengeFixture.createChallengeDto;
import static com.example.dailychallenge.util.fixture.user.UserFixture.USERNAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.dailychallenge.entity.badge.UserBadgeEvaluation;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.UserChallenge;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.repository.badge.BadgeRepository;
import com.example.dailychallenge.repository.badge.UserBadgeEvaluationRepository;
import com.example.dailychallenge.util.ServiceTest;
import com.example.dailychallenge.util.fixture.TestDataSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UserBadgeEvaluationServiceTest extends ServiceTest {
    @Autowired
    private TestDataSetup testDataSetup;
    @Autowired
    private BadgeRepository badgeRepository;
    @Autowired
    private UserBadgeEvaluationService userBadgeEvaluationService;
    @Autowired
    private UserBadgeEvaluationRepository userBadgeEvaluationRepository;

    private User user;

    @BeforeEach
    void beforeEach() {
        user = testDataSetup.saveUser(USERNAME, EMAIL, PASSWORD);
        UserBadgeEvaluation userBadgeEvaluation = UserBadgeEvaluation.builder()
                .users(user)
                .build();
        userBadgeEvaluationRepository.save(userBadgeEvaluation);

        for (int i = 0; i < 9; i++) {
            챌린지를_생성하고_참여하고_달성한다();

            userBadgeEvaluation.addNumberOfAchievement();
            userBadgeEvaluation.addNumberOfChallengeCreate();
        }
    }

    @Nested
    @DisplayName("N개 달성 뱃지")
    class AchievementBadge {
        @Test
        @DisplayName("생성 테스트")
        void canCreateTest() {
            챌린지를_생성하고_참여하고_달성한다();

            userBadgeEvaluationService.createAchievementBadgeIfFollowStandard(user);

            assertEquals("챌린지 10개 달성", badgeRepository.findAll().get(0).getName());
        }

        @Test
        @DisplayName("생성하지 못하는 테스트")
        void canNotCreateTest() {
            for (int i = 0; i < 2; i++) {
                챌린지를_생성하고_참여하고_달성한다();
                UserBadgeEvaluation userBadgeEvaluation = user.getUserBadgeEvaluation();
                userBadgeEvaluation.addNumberOfAchievement();
            }

            userBadgeEvaluationService.createAchievementBadgeIfFollowStandard(user);

            assertTrue(badgeRepository.findAll().isEmpty());
        }
    }

    @Nested
    @DisplayName("챌린지 N개 생성 뱃지")
    class ChallengeCreateBadge {
        @Test
        @DisplayName("생성 테스트")
        void canCreateTest() {
            Challenge challenge = testDataSetup.챌린지를_생성한다(createChallengeDto(), user);
            testDataSetup.챌린지에_참가한다(challenge, user);

            userBadgeEvaluationService.createChallengeCreateBadgeIfFollowStandard(user);

            assertEquals("챌린지 10개 생성", badgeRepository.findAll().get(0).getName());
        }

        @Test
        @DisplayName("생성하지 못하는 테스트")
        void canNotCreateTest() {
            for (int i = 0; i < 2; i++) {
                Challenge challenge = testDataSetup.챌린지를_생성한다(createChallengeDto(), user);
                testDataSetup.챌린지에_참가한다(challenge, user);
                UserBadgeEvaluation userBadgeEvaluation = user.getUserBadgeEvaluation();
                userBadgeEvaluation.addNumberOfChallengeCreate();
            }

            userBadgeEvaluationService.createChallengeCreateBadgeIfFollowStandard(user);

            assertTrue(badgeRepository.findAll().isEmpty());
        }
    }

    private void 챌린지를_생성하고_참여하고_달성한다() {
        Challenge challenge = testDataSetup.챌린지를_생성한다(createChallengeDto(), user);
        UserChallenge userChallenge = testDataSetup.챌린지에_참가한다(challenge, user);
        testDataSetup.챌린지를_달성한다(userChallenge);
    }
}