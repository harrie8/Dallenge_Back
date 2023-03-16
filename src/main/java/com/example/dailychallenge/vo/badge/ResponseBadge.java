package com.example.dailychallenge.vo.badge;

import com.example.dailychallenge.entity.badge.Badge;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseBadge {
    private String createBadgeName;
    private String badgeImgUrl;

    @Builder
    public ResponseBadge(String createBadgeName, String badgeImgUrl) {
        this.createBadgeName = createBadgeName;
        this.badgeImgUrl = badgeImgUrl;
    }

    public static ResponseBadge create(Badge badge) {
        return ResponseBadge.builder()
                .createBadgeName(badge.getName())
                .badgeImgUrl(badge.getImgUrl())
                .build();
    }
}
