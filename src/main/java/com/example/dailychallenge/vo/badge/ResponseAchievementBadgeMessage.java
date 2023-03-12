package com.example.dailychallenge.vo.badge;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseAchievementBadgeMessage {

    private Integer code;
    private String message;
    private String createBadgeName;

    @Builder
    public ResponseAchievementBadgeMessage(Integer code, String message, String createBadgeName) {
        this.code = code;
        this.message = message;
        this.createBadgeName = createBadgeName;
    }
}
