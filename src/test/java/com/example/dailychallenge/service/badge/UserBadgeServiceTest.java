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
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class UserBadgeServiceTest extends ServiceTest {
    @Autowired
    private TestDataSetup testDataSetup;
    @Autowired
    private BadgeRepository badgeRepository;
    @Autowired
    private BadgeService badgeService;
    @Autowired
    private UserBadgeRepository userBadgeRepository;
    @Autowired
    private UserBadgeService userBadgeService;

    @Test
    @DisplayName("유저를 생성할 때 모든 챌린지 생성 뱃지들의 상태가 false인 테스트")
    void allChallengeCreateUserBadgeStatusIsFalseWhenUserCreateTest() {
        User user = testDataSetup.saveUser(USERNAME, EMAIL, PASSWORD);
        List<String> badgeNames = Arrays.stream(ChallengeCreateBadgeType.values())
                .map(ChallengeCreateBadgeType::getName)
                .collect(Collectors.toUnmodifiableList());
        List<Badge> badges = badgeService.createBadges(badgeNames);

        userBadgeService.createUserBadges(user, badges);

        assertEquals(5, badgeRepository.findAll().size());
        assertTrue(userBadgeRepository.findAll().stream().allMatch(userBadge -> userBadge.getStatus().equals(false)));
    }

    @Test
    @DisplayName("유저를 생성할 때 모든 챌린지 달성 뱃지들의 상태가 false인 테스트")
    void allAchievementUserBadgeStatusIsFalseWhenUserCreateTest() {
        User user = testDataSetup.saveUser(USERNAME, EMAIL, PASSWORD);
        List<String> badgeNames = Arrays.stream(AchievementBadgeType.values())
                .map(AchievementBadgeType::getName)
                .collect(Collectors.toUnmodifiableList());
        List<Badge> badges = badgeService.createBadges(badgeNames);

        userBadgeService.createUserBadges(user, badges);

        assertEquals(5, badgeRepository.findAll().size());
        assertTrue(userBadgeRepository.findAll().stream().allMatch(userBadge -> userBadge.getStatus().equals(false)));
    }

    @Test
    @DisplayName("유저를 생성할 때 모든 후기 작성 뱃지들의 상태가 false인 테스트")
    void allCommentWriteUserBadgeStatusIsFalseWhenUserCreateTest() {
        User user = testDataSetup.saveUser(USERNAME, EMAIL, PASSWORD);
        List<String> badgeNames = Arrays.stream(CommentWriteBadgeType.values())
                .map(CommentWriteBadgeType::getName)
                .collect(Collectors.toUnmodifiableList());
        List<Badge> badges = badgeService.createBadges(badgeNames);

        userBadgeService.createUserBadges(user, badges);

        assertEquals(5, badgeRepository.findAll().size());
        assertTrue(userBadgeRepository.findAll().stream().allMatch(userBadge -> userBadge.getStatus().equals(false)));
    }

    @Test
    @DisplayName("유저 ID와 뱃지 이름으로 찾는 테스트")
    void findByUserIdAndBadgeNameTest() {
        User user = testDataSetup.saveUser(USERNAME, EMAIL, PASSWORD);
        List<String> badgeNames = Arrays.stream(ChallengeCreateBadgeType.values())
                .map(ChallengeCreateBadgeType::getName)
                .collect(Collectors.toUnmodifiableList());
        List<Badge> badges = badgeService.createBadges(badgeNames);
        userBadgeService.createUserBadges(user, badges);

        UserBadge byUserIdAndBadgeName = userBadgeService.findByUsersIdAndBadgeName(user.getId(), "챌린지 10개 생성");

        assertEquals("챌린지 10개 생성", byUserIdAndBadgeName.getBadge().getName());
        assertEquals(false, byUserIdAndBadgeName.getStatus());
    }
}