package com.example.dailychallenge.vo.badge;

import com.example.dailychallenge.entity.badge.Badge;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseBadge {
    private String badgeName;
    private Boolean badgeStatus;
    private String badgeImgUrl;

    @Builder
    public ResponseBadge(String badgeName, Boolean badgeStatus, String badgeImgUrl) {
        this.badgeName = badgeName;
        this.badgeStatus = badgeStatus;
        this.badgeImgUrl = badgeImgUrl;
    }

    // 정렬 기준: 달성, 후기, 생성 순으로 정렬된다
    public static List<ResponseBadge> create(List<Badge> badges) {
        return badges.stream()
                .map(badge -> ResponseBadge.builder()
                        .badgeName(badge.getName())
                        .badgeStatus(false)
                        .badgeImgUrl(badge.getImgUrl())
                        .build())
                .collect(Collectors.toUnmodifiableList());
    }
}
