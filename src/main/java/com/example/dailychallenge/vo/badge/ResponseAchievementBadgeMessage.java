package com.example.dailychallenge.vo.badge;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseAchievementBadgeMessage {

    private Integer code;
    private String message;
    private ResponseBadge badgeInfo;

    @Builder
    public ResponseAchievementBadgeMessage(Integer code, String message, ResponseBadge responseBadge) {
        this.code = code;
        this.message = message;
        this.badgeInfo = responseBadge;
    }
}
