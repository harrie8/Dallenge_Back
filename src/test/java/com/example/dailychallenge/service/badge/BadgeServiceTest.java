package com.example.dailychallenge.service.badge;

import static com.example.dailychallenge.util.fixture.TokenFixture.EMAIL;
import static com.example.dailychallenge.util.fixture.TokenFixture.PASSWORD;
import static com.example.dailychallenge.util.fixture.user.UserFixture.USERNAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.dailychallenge.entity.badge.Badge;
import com.example.dailychallenge.entity.badge.UserBadge;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.repository.badge.BadgeRepository;
import com.example.dailychallenge.repository.badge.UserBadgeRepository;
import com.example.dailychallenge.util.ServiceTest;
import com.example.dailychallenge.util.fixture.TestDataSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

    @Test
    @DisplayName("뱃지 생성 테스트")
    void createBadgeTest() {
        String badgeName = "챌린지 10개 생성";

        Badge badge = badgeService.createBadge(badgeName);

        assertEquals(badgeName, badge.getName());
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
