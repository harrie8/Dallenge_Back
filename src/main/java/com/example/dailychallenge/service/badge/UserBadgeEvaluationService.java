package com.example.dailychallenge.service.badge;

import com.example.dailychallenge.entity.badge.Badge;
import com.example.dailychallenge.entity.badge.UserBadgeEvaluation;
import com.example.dailychallenge.entity.badge.type.AchievementBadgeType;
import com.example.dailychallenge.entity.badge.type.BadgeType;
import com.example.dailychallenge.entity.badge.type.ChallengeCreateBadgeType;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.repository.badge.UserBadgeEvaluationRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserBadgeEvaluationService {
    private final UserBadgeEvaluationRepository userBadgeEvaluationRepository;
    private final BadgeService badgeService;
    private final UserBadgeService userBadgeService;

    @Transactional
    public UserBadgeEvaluation createUserBadgeEvaluation(User user) {
        return userBadgeEvaluationRepository.save(UserBadgeEvaluation.builder()
                .users(user)
                .build());
    }

    @Transactional
    public Optional<Badge> createAchievementBadgeIfFollowStandard(User user) {
        UserBadgeEvaluation userBadgeEvaluation = user.getUserBadgeEvaluation();

        userBadgeEvaluation.addNumberOfAchievement();
        Integer numberOfAchievement = userBadgeEvaluation.getNumberOfAchievement();

        Optional<AchievementBadgeType> optionalAchievementBadgeType = AchievementBadgeType
                .findByNumber(numberOfAchievement);
        if (optionalAchievementBadgeType.isPresent()) {
            Badge badge = createBadge(user, optionalAchievementBadgeType.get());
            return Optional.of(badge);
        }
        return Optional.empty();
    }

    @Transactional
    public Optional<Badge> createChallengeCreateBadgeIfFollowStandard(User user) {
        UserBadgeEvaluation userBadgeEvaluation = user.getUserBadgeEvaluation();

        userBadgeEvaluation.addNumberOfChallengeCreate();
        Integer numberOfChallengeCreate = userBadgeEvaluation.getNumberOfChallengeCreate();

        Optional<ChallengeCreateBadgeType> optionalChallengeCreateBadgeType = ChallengeCreateBadgeType
                .findByNumber(numberOfChallengeCreate);
        if (optionalChallengeCreateBadgeType.isPresent()) {
            Badge badge = createBadge(user, optionalChallengeCreateBadgeType.get());
            return Optional.of(badge);
        }
        return Optional.empty();
    }

    private Badge createBadge(User user, BadgeType badgeType) {
        String badgeName = badgeType.getName();
        Badge badge = badgeService.createBadge(badgeName);
        userBadgeService.createUserBadge(user, badge);

        return badge;
    }
}
