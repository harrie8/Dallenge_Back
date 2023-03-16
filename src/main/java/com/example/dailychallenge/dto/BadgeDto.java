package com.example.dailychallenge.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BadgeDto {

    private String badgeName;
    private String badgeImgFileName;

    @Builder
    public BadgeDto(String badgeName, String badgeImgFileName) {
        this.badgeName = badgeName;
        this.badgeImgFileName = badgeImgFileName;
    }
}
