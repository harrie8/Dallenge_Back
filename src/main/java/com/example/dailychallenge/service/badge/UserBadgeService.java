package com.example.dailychallenge.service.badge;

import com.example.dailychallenge.dto.BadgeDto;
import com.example.dailychallenge.entity.badge.Badge;
import com.example.dailychallenge.entity.badge.UserBadge;
import com.example.dailychallenge.entity.badge.type.AchievementBadgeType;
import com.example.dailychallenge.entity.badge.type.ChallengeCreateBadgeType;
import com.example.dailychallenge.entity.badge.type.CommentWriteBadgeType;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.exception.badge.UserBadgeNotFound;
import com.example.dailychallenge.repository.badge.UserBadgeRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserBadgeService {
    private final UserBadgeRepository userBadgeRepository;
    private final BadgeService badgeService;

    public void saveUserBadges(User savedUser) {
        List<Badge> badges = badgeService.findAll();
        if (badges.isEmpty()) {
            saveBadges(savedUser);
            return;
        }
        createUserBadges(savedUser, badges);
    }

    public void saveBadges(User savedUser) {
        List<BadgeDto> achievementBadgeDtos = AchievementBadgeType.getBadgeDtos();
        List<BadgeDto> commentWriteBadgeDtos = CommentWriteBadgeType.getBadgeDtos();
        List<BadgeDto> challengeCreateBadgeDtos = ChallengeCreateBadgeType.getBadgeDtos();
        List<Badge> achievementBadges = badgeService.createBadges(achievementBadgeDtos);
        List<Badge> commentWriteBadges = badgeService.createBadges(commentWriteBadgeDtos);
        List<Badge> challengeCreateBadges = badgeService.createBadges(challengeCreateBadgeDtos);
        createUserBadges(savedUser, achievementBadges);
        createUserBadges(savedUser, commentWriteBadges);
        createUserBadges(savedUser, challengeCreateBadges);
    }


    public List<UserBadge> createUserBadges(User user, List<Badge> badges) {
        List<UserBadge> userBadges = badges.stream()
                .map(badge -> UserBadge.builder()
                        .status(false)
                        .users(user)
                        .badge(badge)
                        .build()
                )
                .collect(Collectors.toUnmodifiableList());

        return userBadgeRepository.saveAll(userBadges);
    }

    public List<UserBadge> findAllByUserId(Long userId) {
        return userBadgeRepository.findAllByUsersId(userId);
    }

    public UserBadge findByUsersIdAndBadgeName(Long userId, String badgeName) {
        return userBadgeRepository.findByUsersIdAndBadgeName(userId, badgeName)
                .orElseThrow(UserBadgeNotFound::new);
    }
}
