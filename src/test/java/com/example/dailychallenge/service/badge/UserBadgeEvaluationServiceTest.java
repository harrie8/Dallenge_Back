package com.example.dailychallenge.service.badge;

import static com.example.dailychallenge.util.fixture.TokenFixture.EMAIL;
import static com.example.dailychallenge.util.fixture.TokenFixture.PASSWORD;
import static com.example.dailychallenge.util.fixture.challenge.ChallengeFixture.createChallengeDto;
import static com.example.dailychallenge.util.fixture.user.UserFixture.OTHER_EMAIL;
import static com.example.dailychallenge.util.fixture.user.UserFixture.OTHER_USERNAME;
import static com.example.dailychallenge.util.fixture.user.UserFixture.USERNAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.dailychallenge.entity.badge.UserBadge;
import com.example.dailychallenge.entity.badge.UserBadgeEvaluation;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.UserChallenge;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.repository.badge.BadgeRepository;
import com.example.dailychallenge.repository.badge.UserBadgeRepository;
import com.example.dailychallenge.util.ServiceTest;
import com.example.dailychallenge.util.fixture.TestDataSetup;
import java.util.List;
import java.util.Optional;
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
    private UserBadgeRepository userBadgeRepository;
    @Autowired
    private UserBadgeEvaluationService userBadgeEvaluationService;

    private User user;

    @BeforeEach
    void beforeEach() {
        user = testDataSetup.saveUser(USERNAME, EMAIL, PASSWORD);
        UserBadgeEvaluation userBadgeEvaluation = testDataSetup.saveUserBadgeEvaluation(user);
        testDataSetup.saveBadgesAndUserBadges(user);

        for (int i = 0; i < 9; i++) {
            챌린지를_생성하고_참여하고_달성한다();

            userBadgeEvaluation.addNumberOfAchievement();
            userBadgeEvaluation.addNumberOfChallengeCreate();
        }
    }

    @Nested
    @DisplayName("챌린지 N개 달성 뱃지")
    class AchievementBadge {
        @Test
        @DisplayName("생성 테스트")
        void canCreateTest() {
            챌린지를_생성하고_참여하고_달성한다();

            userBadgeEvaluationService.createAchievementBadgeIfFollowStandard(user);

            assertEquals(15, badgeRepository.findAll().size());
            List<UserBadge> allByUsersId = userBadgeRepository.findAllByUsersId(user.getId());
            Optional<UserBadge> optionalUserBadge = allByUsersId.stream()
                    .filter(userBadge -> userBadge.getStatus().equals(true))
                    .findFirst();
            optionalUserBadge.ifPresent(userBadge -> assertEquals("챌린지 10개 달성", userBadge.getBadge().getName()));
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

            assertEquals(15, badgeRepository.findAll().size());
            assertTrue(userBadgeRepository.findAll().stream().allMatch(userBadge -> userBadge.getStatus().equals(false)));
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

            assertEquals(15, badgeRepository.findAll().size());
            List<UserBadge> allByUsersId = userBadgeRepository.findAllByUsersId(user.getId());
            Optional<UserBadge> optionalUserBadge = allByUsersId.stream()
                    .filter(userBadge -> userBadge.getStatus().equals(true))
                    .findFirst();
            optionalUserBadge.ifPresent(userBadge -> assertEquals("챌린지 10개 생성", userBadge.getBadge().getName()));
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

            assertEquals(15, badgeRepository.findAll().size());
            assertTrue(userBadgeRepository.findAll().stream().allMatch(userBadge -> userBadge.getStatus().equals(false)));
        }
    }

    @Nested
    @DisplayName("후기 N개 작성 뱃지")
    class CommentWriteBadge {
        @Test
        @DisplayName("생성 테스트")
        void canCreateTest() {
            UserBadgeEvaluation userBadgeEvaluation = user.getUserBadgeEvaluation();
            User otherUser = testDataSetup.saveUser(OTHER_USERNAME, OTHER_EMAIL, PASSWORD);
            for (int i = 0; i < 9; i++) {
                Challenge challenge = testDataSetup.챌린지를_생성한다(createChallengeDto(), otherUser);
                testDataSetup.챌린지에_참가한다(challenge, otherUser);
                testDataSetup.챌린지에_참가한다(challenge, user);

                testDataSetup.챌린지에_댓글을_단다(challenge, user, "content" + i);
                userBadgeEvaluation.addNumberOfCommentWrite();
            }
            Challenge challenge = testDataSetup.챌린지를_생성한다(createChallengeDto(), otherUser);
            testDataSetup.챌린지에_댓글을_단다(challenge, user, "content" + 9);

            userBadgeEvaluationService.createCommentWriteBadgeIfFollowStandard(user);

            assertEquals(15, badgeRepository.findAll().size());
            List<UserBadge> allByUsersId = userBadgeRepository.findAllByUsersId(user.getId());
            Optional<UserBadge> optionalUserBadge = allByUsersId.stream()
                    .filter(userBadge -> userBadge.getStatus().equals(true))
                    .findFirst();
            optionalUserBadge.ifPresent(userBadge -> assertEquals("후기 10개 작성", userBadge.getBadge().getName()));
        }

        @Test
        @DisplayName("생성하지 못하는 테스트")
        void canNotCreateTest() {
            UserBadgeEvaluation userBadgeEvaluation = user.getUserBadgeEvaluation();
            User otherUser = testDataSetup.saveUser(OTHER_USERNAME, OTHER_EMAIL, PASSWORD);
            for (int i = 0; i < 2; i++) {
                Challenge challenge = testDataSetup.챌린지를_생성한다(createChallengeDto(), otherUser);
                testDataSetup.챌린지에_참가한다(challenge, otherUser);
                testDataSetup.챌린지에_참가한다(challenge, user);

                testDataSetup.챌린지에_댓글을_단다(challenge, user, "content" + i);
                userBadgeEvaluation.addNumberOfCommentWrite();
            }
            Challenge challenge = testDataSetup.챌린지를_생성한다(createChallengeDto(), otherUser);
            testDataSetup.챌린지에_댓글을_단다(challenge, user, "content" + 3);

            userBadgeEvaluationService.createCommentWriteBadgeIfFollowStandard(user);

            assertEquals(15, badgeRepository.findAll().size());
            assertTrue(userBadgeRepository.findAll().stream().allMatch(userBadge -> userBadge.getStatus().equals(false)));
        }
    }

    private void 챌린지를_생성하고_참여하고_달성한다() {
        Challenge challenge = testDataSetup.챌린지를_생성한다(createChallengeDto(), user);
        UserChallenge userChallenge = testDataSetup.챌린지에_참가한다(challenge, user);
        testDataSetup.챌린지를_달성한다(userChallenge);
    }
}