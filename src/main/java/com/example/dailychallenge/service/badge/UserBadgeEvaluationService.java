package com.example.dailychallenge.service.badge;

import com.example.dailychallenge.entity.badge.Badge;
import com.example.dailychallenge.entity.badge.UserBadge;
import com.example.dailychallenge.entity.badge.UserBadgeEvaluation;
import com.example.dailychallenge.entity.badge.type.AchievementBadgeType;
import com.example.dailychallenge.entity.badge.type.ChallengeCreateBadgeType;
import com.example.dailychallenge.entity.badge.type.CommentWriteBadgeType;
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
            Long userId = user.getId();
            String badgeName = optionalAchievementBadgeType.get().getName();
            UserBadge userBadge = userBadgeService.findByUsersIdAndBadgeName(userId, badgeName);
            userBadge.setStatusToTrue();
            Badge badge = userBadge.getBadge();
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
            Long userId = user.getId();
            String badgeName = optionalChallengeCreateBadgeType.get().getName();
            UserBadge userBadge = userBadgeService.findByUsersIdAndBadgeName(userId, badgeName);
            userBadge.setStatusToTrue();
            Badge badge = userBadge.getBadge();
            return Optional.of(badge);
        }
        return Optional.empty();
    }

    @Transactional
    public Optional<Badge> createCommentWriteBadgeIfFollowStandard(User user) {
        UserBadgeEvaluation userBadgeEvaluation = user.getUserBadgeEvaluation();

        userBadgeEvaluation.addNumberOfCommentWrite();
        Integer numberOfCommentWrite = userBadgeEvaluation.getNumberOfCommentWrite();
        Optional<CommentWriteBadgeType> optionalCommentWriteBadgeType = CommentWriteBadgeType
                .findByNumber(numberOfCommentWrite);
        if (optionalCommentWriteBadgeType.isPresent()) {
            Long userId = user.getId();
            String badgeName = optionalCommentWriteBadgeType.get().getName();
            UserBadge userBadge = userBadgeService.findByUsersIdAndBadgeName(userId, badgeName);
            userBadge.setStatusToTrue();
            Badge badge = userBadge.getBadge();
            return Optional.of(badge);
        }
        return Optional.empty();
    }
}
