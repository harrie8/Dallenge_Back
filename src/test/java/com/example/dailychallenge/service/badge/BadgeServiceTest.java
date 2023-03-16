package com.example.dailychallenge.service.badge;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.dailychallenge.dto.BadgeDto;
import com.example.dailychallenge.entity.badge.Badge;
import com.example.dailychallenge.entity.badge.type.AchievementBadgeType;
import com.example.dailychallenge.entity.badge.type.ChallengeCreateBadgeType;
import com.example.dailychallenge.entity.badge.type.CommentWriteBadgeType;
import com.example.dailychallenge.repository.badge.BadgeRepository;
import com.example.dailychallenge.util.ServiceTest;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

class BadgeServiceTest extends ServiceTest {
    @Value("${defaultBadgeImgLocation}")
    private String badgeImgLocation;
    @Value("${badgeImgFileExtension}")
    private String badgeImgFileExtension;

    @Autowired
    private BadgeService badgeService;
    @Autowired
    private BadgeRepository badgeRepository;

    @Nested
    @DisplayName("뱃지들 생성 테스트")
    class createBadgesTest {
        @Test
        @DisplayName("모든 챌린지 생성 뱃지들 생성 테스트")
        void createAllCreateChallengeBadgesTest() {
            List<BadgeDto> badgeDtos = ChallengeCreateBadgeType.getBadgeDtos();

            List<Badge> badges = badgeService.createBadges(badgeDtos);

            List<Badge> allBadges = badgeRepository.findAll();
            assertEquals(badges.size(), allBadges.size());
            List<String> actualBadgeNames = allBadges.stream()
                    .map(Badge::getName)
                    .collect(Collectors.toUnmodifiableList());
            List<String> expectBadgeNames = badgeDtos.stream()
                    .map(BadgeDto::getBadgeName)
                    .collect(Collectors.toUnmodifiableList());
            assertEquals(expectBadgeNames, actualBadgeNames);
            List<String> actualBadgeImgUrls = allBadges.stream()
                    .map(Badge::getImgUrl)
                    .collect(Collectors.toUnmodifiableList());
            List<String> expectBadgeImgUrls = badgeDtos.stream()
                    .map(badgeDto -> badgeImgLocation + badgeDto.getBadgeImgFileName() + badgeImgFileExtension)
                    .collect(Collectors.toUnmodifiableList());
            assertEquals(expectBadgeImgUrls, actualBadgeImgUrls);
        }

        @Test
        @DisplayName("모든 챌린지 달성 뱃지들 생성 테스트")
        void createAllAchievementChallengeBadgesTest() {
            List<BadgeDto> badgeDtos = AchievementBadgeType.getBadgeDtos();

            List<Badge> badges = badgeService.createBadges(badgeDtos);

            List<Badge> allBadges = badgeRepository.findAll();
            assertEquals(badges.size(), allBadges.size());
            List<String> actualBadgeNames = allBadges.stream()
                    .map(Badge::getName)
                    .collect(Collectors.toUnmodifiableList());
            List<String> expectBadgeNames = badgeDtos.stream()
                    .map(BadgeDto::getBadgeName)
                    .collect(Collectors.toUnmodifiableList());
            assertEquals(expectBadgeNames, actualBadgeNames);
            List<String> actualBadgeImgUrls = allBadges.stream()
                    .map(Badge::getImgUrl)
                    .collect(Collectors.toUnmodifiableList());
            List<String> expectBadgeImgUrls = badgeDtos.stream()
                    .map(badgeDto -> badgeImgLocation + badgeDto.getBadgeImgFileName() + badgeImgFileExtension)
                    .collect(Collectors.toUnmodifiableList());
            assertEquals(expectBadgeImgUrls, actualBadgeImgUrls);
        }

        @Test
        @DisplayName("모든 후기 작성 뱃지들 생성 테스트")
        void createAllCommentWriteBadgesTest() {
            List<BadgeDto> badgeDtos = CommentWriteBadgeType.getBadgeDtos();

            List<Badge> badges = badgeService.createBadges(badgeDtos);

            List<Badge> allBadges = badgeRepository.findAll();
            assertEquals(badges.size(), allBadges.size());
            List<String> actualBadgeNames = allBadges.stream()
                    .map(Badge::getName)
                    .collect(Collectors.toUnmodifiableList());
            List<String> expectBadgeNames = badgeDtos.stream()
                    .map(BadgeDto::getBadgeName)
                    .collect(Collectors.toUnmodifiableList());
            assertEquals(expectBadgeNames, actualBadgeNames);
            List<String> actualBadgeImgUrls = allBadges.stream()
                    .map(Badge::getImgUrl)
                    .collect(Collectors.toUnmodifiableList());
            List<String> expectBadgeImgUrls = badgeDtos.stream()
                    .map(badgeDto -> badgeImgLocation + badgeDto.getBadgeImgFileName() + badgeImgFileExtension)
                    .collect(Collectors.toUnmodifiableList());
            assertEquals(expectBadgeImgUrls, actualBadgeImgUrls);
        }
    }
}
