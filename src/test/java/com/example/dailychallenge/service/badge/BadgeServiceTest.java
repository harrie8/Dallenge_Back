package com.example.dailychallenge.service.badge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.dailychallenge.entity.badge.Badge;
import com.example.dailychallenge.repository.badge.BadgeRepository;
import com.example.dailychallenge.util.ServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class BadgeServiceTest extends ServiceTest {
    @Autowired
    private BadgeService badgeService;
    @Autowired
    private BadgeRepository badgeRepository;

    @Test
    @DisplayName("뱃지 생성 테스트")
    void createBadgeTest() {
        String badgeName = "5일 연속 달성";

        Badge badge = badgeService.createBadge(badgeName);

        assertEquals(badgeName, badge.getName());
    }

    @Test
    @DisplayName("뱃지 삭제 테스트")
    void removeBadgeTest() {
        String badgeName = "5일 연속 달성";
        Badge badge = badgeService.createBadge(badgeName);

        badgeService.removeBadge(badge);

        assertTrue(badgeRepository.findById(badge.getId()).isEmpty());
    }
}
