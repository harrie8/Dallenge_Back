package com.example.dailychallenge.service.badge;

import com.example.dailychallenge.entity.badge.Badge;
import com.example.dailychallenge.entity.badge.UserBadgeEvaluation;
import com.example.dailychallenge.entity.badge.type.AchievementBadgeType;
import com.example.dailychallenge.entity.badge.type.BadgeType;
import com.example.dailychallenge.entity.badge.type.ChallengeCreatetBadgeType;
import com.example.dailychallenge.entity.users.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserBadgeEvaluationService {
    private final BadgeService badgeService;
    private final UserBadgeService userBadgeService;

    @Transactional
    public void createAchievementBadgeIfFollowStandard(User user) {
        UserBadgeEvaluation userBadgeEvaluation = user.getUserBadgeEvaluation();

        userBadgeEvaluation.addNumberOfAchievement();
        Integer numberOfAchievement = userBadgeEvaluation.getNumberOfAchievement();

        AchievementBadgeType
                .findByNumber(numberOfAchievement)
                .ifPresent(achievementBadgeType -> createBadge(user, achievementBadgeType));
    }

    @Transactional
    public void createChallengeCreateBadgeIfFollowStandard(User user) {
        UserBadgeEvaluation userBadgeEvaluation = user.getUserBadgeEvaluation();

        userBadgeEvaluation.addNumberOfChallengeCreate();
        Integer numberOfChallengeCreate = userBadgeEvaluation.getNumberOfChallengeCreate();

        ChallengeCreatetBadgeType
                .findByNumber(numberOfChallengeCreate)
                .ifPresent(challengeCreatetBadgeType -> createBadge(user, challengeCreatetBadgeType));
    }

    private void createBadge(User user, BadgeType badgeType) {
        String badgeName = badgeType.getName();
        Badge badge = badgeService.createBadge(badgeName);
        userBadgeService.createUserBadge(user, badge);
    }
}
