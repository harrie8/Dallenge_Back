package com.example.dailychallenge.vo.badge;

import com.example.dailychallenge.entity.badge.Badge;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseCreateBadge {
    private String createBadgeName;
    private String badgeImgUrl;

    @Builder
    public ResponseCreateBadge(String createBadgeName, String badgeImgUrl) {
        this.createBadgeName = createBadgeName;
        this.badgeImgUrl = badgeImgUrl;
    }

    public static ResponseCreateBadge create(Badge badge) {
        return ResponseCreateBadge.builder()
                .createBadgeName(badge.getName())
                .badgeImgUrl(badge.getImgUrl())
                .build();
    }
}
