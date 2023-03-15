package com.example.dailychallenge.service.badge;

import static com.example.dailychallenge.util.fixture.TokenFixture.EMAIL;
import static com.example.dailychallenge.util.fixture.TokenFixture.PASSWORD;
import static com.example.dailychallenge.util.fixture.user.UserFixture.USERNAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.dailychallenge.entity.badge.Badge;
import com.example.dailychallenge.entity.badge.UserBadge;
import com.example.dailychallenge.entity.badge.type.AchievementBadgeType;
import com.example.dailychallenge.entity.badge.type.ChallengeCreateBadgeType;
import com.example.dailychallenge.entity.badge.type.CommentWriteBadgeType;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.repository.badge.BadgeRepository;
import com.example.dailychallenge.repository.badge.UserBadgeRepository;
import com.example.dailychallenge.util.ServiceTest;
import com.example.dailychallenge.util.fixture.TestDataSetup;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

class BadgeServiceTest extends ServiceTest {
    @Autowired
    private BadgeService badgeService;
    @Autowired
    private BadgeRepository badgeRepository;
    @Autowired
    private TestDataSetup testDataSetup;
    @Autowired
    private UserBadgeRepository userBadgeRepository;

    private User user;

    @BeforeEach
    void beforeEach() {
        user = testDataSetup.saveUser(USERNAME, EMAIL, PASSWORD);
    }

    @Nested
    @DisplayName("뱃지들 생성 테스트")
    class createBadgesTest {
        @Test
        @DisplayName("모든 챌린지 생성 뱃지들 생성 테스트")
        void createAllCreateChallengeBadgesTest() {
            List<String> badgeNames = Arrays.stream(ChallengeCreateBadgeType.values())
                    .map(ChallengeCreateBadgeType::getName)
                    .collect(Collectors.toUnmodifiableList());

            List<Badge> badges = badgeService.createBadges(badgeNames);

            assertEquals(badges.size(), badgeRepository.findAll().size());
        }

        @Test
        @DisplayName("모든 챌린지 달성 뱃지들 생성 테스트")
        void createAllAchievementChallengeBadgesTest() {
            List<String> badgeNames = Arrays.stream(AchievementBadgeType.values())
                    .map(AchievementBadgeType::getName)
                    .collect(Collectors.toUnmodifiableList());

            List<Badge> badges = badgeService.createBadges(badgeNames);

            assertEquals(badges.size(), badgeRepository.findAll().size());
        }

        @Test
        @DisplayName("모든 후기 작성 뱃지들 생성 테스트")
        void createAllCommentWriteBadgesTest() {
            List<String> badgeNames = Arrays.stream(CommentWriteBadgeType.values())
                    .map(CommentWriteBadgeType::getName)
                    .collect(Collectors.toUnmodifiableList());

            List<Badge> badges = badgeService.createBadges(badgeNames);

            assertEquals(badges.size(), badgeRepository.findAll().size());
        }
    }


    @Test
    @DisplayName("뱃지 삭제 테스트")
    void removeBadgeTest() {
        String badgeName = "챌린지 10개 생성";
        Badge badge = badgeService.createBadge(badgeName);

        badgeService.removeBadge(badge);

        assertTrue(badgeRepository.findById(badge.getId()).isEmpty());
    }

    @Test
    @DisplayName("뱃지 연관관계 삭제 테스트")
    @Transactional
    void removeBadgeAndUserBadgeTest() {
        String badgeName = "챌린지 10개 생성";
        Badge badge = badgeService.createBadge(badgeName);
        UserBadge userBadge = userBadgeRepository.save(UserBadge.builder()
                .users(user)
                .badge(badge)
                .build());

        badgeService.removeBadge(badge);

        assertTrue(badgeRepository.findById(badge.getId()).isEmpty());
        assertTrue(userBadgeRepository.findById(userBadge.getId()).isEmpty());
    }
}
