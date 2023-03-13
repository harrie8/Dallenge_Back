package com.example.dailychallenge.vo.badge;

import com.example.dailychallenge.entity.badge.UserBadge;
import com.example.dailychallenge.entity.badge.type.AchievementBadgeType;
import com.example.dailychallenge.entity.badge.type.ChallengeCreateBadgeType;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseUserBadge {
    private List<String> challengeCreateBadgeNames;
    private List<String> achievementBadgeNames;

    @Builder
    public ResponseUserBadge(List<String> challengeCreateBadgeNames, List<String> achievementBadgeNames) {
        this.challengeCreateBadgeNames = challengeCreateBadgeNames;
        this.achievementBadgeNames = achievementBadgeNames;
    }

    public static ResponseUserBadge create(List<UserBadge> userBadges) {
        List<String> challengeCreateBadgeNames = makeChallengeCreateBadgeNames(userBadges);
        List<String> achievementBadgeNames = makeAchievementBadgeNames(userBadges);

        return ResponseUserBadge.builder()
                .challengeCreateBadgeNames(challengeCreateBadgeNames)
                .achievementBadgeNames(achievementBadgeNames)
                .build();
    }

    private static List<String> makeChallengeCreateBadgeNames(List<UserBadge> userBadges) {
        return userBadges.stream()
                .map(userBadge -> userBadge.getBadge().getName())
                .filter(ChallengeCreateBadgeType::isSameType)
                .sorted()
                .collect(Collectors.toUnmodifiableList());
    }

    private static List<String> makeAchievementBadgeNames(List<UserBadge> userBadges) {
        return userBadges.stream()
                .map(userBadge -> userBadge.getBadge().getName())
                .filter(AchievementBadgeType::isSameType)
                .sorted()
                .collect(Collectors.toUnmodifiableList());
    }
}
