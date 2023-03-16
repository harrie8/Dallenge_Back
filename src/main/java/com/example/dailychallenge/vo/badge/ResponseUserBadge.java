package com.example.dailychallenge.vo.badge;

import com.example.dailychallenge.entity.badge.Badge;
import com.example.dailychallenge.entity.badge.UserBadge;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseUserBadge {
    private String badgeName;
    private Boolean badgeStatus;
    private String badgeImgUrl;

    @Builder
    public ResponseUserBadge(String badgeName, Boolean badgeStatus, String badgeImgUrl) {
        this.badgeName = badgeName;
        this.badgeStatus = badgeStatus;
        this.badgeImgUrl = badgeImgUrl;
    }

    public static List<ResponseUserBadge> create(List<UserBadge> userBadges) {
        return userBadges.stream()
                .map(userBadge -> {
                    Badge badge = userBadge.getBadge();
                    Boolean badgeStatus = userBadge.getStatus();
                    return ResponseUserBadge.builder()
                            .badgeName(badge.getName())
                            .badgeStatus(badgeStatus)
                            .badgeImgUrl(badge.getImgUrl())
                            .build();
                }).collect(Collectors.toUnmodifiableList());
    }
}
