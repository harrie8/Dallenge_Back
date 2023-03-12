package com.example.dailychallenge.service.badge;

import com.example.dailychallenge.entity.badge.Badge;
import com.example.dailychallenge.entity.badge.UserBadgeEvaluation;
import com.example.dailychallenge.entity.badge.type.AchievementBadgeType;
import com.example.dailychallenge.entity.badge.type.BadgeType;
import com.example.dailychallenge.entity.badge.type.ChallengeCreateBadgeType;
import com.example.dailychallenge.entity.users.User;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserBadgeEvaluationService {
    private final static String EMPTY_NAME = "";
    private final BadgeService badgeService;
    private final UserBadgeService userBadgeService;

    @Transactional
    public String createAchievementBadgeIfFollowStandard(User user) {
        UserBadgeEvaluation userBadgeEvaluation = user.getUserBadgeEvaluation();

        userBadgeEvaluation.addNumberOfAchievement();
        Integer numberOfAchievement = userBadgeEvaluation.getNumberOfAchievement();

        Optional<AchievementBadgeType> optionalAchievementBadgeType = AchievementBadgeType
                .findByNumber(numberOfAchievement);
        if (optionalAchievementBadgeType.isPresent()) {
            Badge badge = createBadge(user, optionalAchievementBadgeType.get());
            return badge.getName();
        }
        return EMPTY_NAME;
    }

    @Transactional
    public String createChallengeCreateBadgeIfFollowStandard(User user) {
        UserBadgeEvaluation userBadgeEvaluation = user.getUserBadgeEvaluation();

        userBadgeEvaluation.addNumberOfChallengeCreate();
        Integer numberOfChallengeCreate = userBadgeEvaluation.getNumberOfChallengeCreate();

        Optional<ChallengeCreateBadgeType> optionalChallengeCreateBadgeType = ChallengeCreateBadgeType
                .findByNumber(numberOfChallengeCreate);
        if (optionalChallengeCreateBadgeType.isPresent()) {
            Badge badge = createBadge(user, optionalChallengeCreateBadgeType.get());
            return badge.getName();
        }
        return EMPTY_NAME;
    }

    private Badge createBadge(User user, BadgeType badgeType) {
        String badgeName = badgeType.getName();
        Badge badge = badgeService.createBadge(badgeName);
        userBadgeService.createUserBadge(user, badge);

        return badge;
    }
}
