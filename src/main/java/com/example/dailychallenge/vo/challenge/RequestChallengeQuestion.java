package com.example.dailychallenge.vo.challenge;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class RequestChallengeQuestion {
    @NotNull(message = "챌린지 장소는 Null 일 수 없습니다!")
    @Min(0)
    @Max(1)
    private int challengeLocationIndex;
    @NotNull(message = "챌린지 기간은 Null 일 수 없습니다!")
    @Min(0)
    @Max(3)
    private int challengeDurationIndex;
    @NotNull(message = "챌린지 카테고리는 Null 일 수 없습니다!")
    @Min(0)
    @Max(4)
    private int challengeCategoryIndex;

    @Builder
    public RequestChallengeQuestion(int challengeLocationIndex, int challengeDurationIndex, int challengeCategoryIndex) {
        this.challengeLocationIndex = challengeLocationIndex;
        this.challengeDurationIndex = challengeDurationIndex;
        this.challengeCategoryIndex = challengeCategoryIndex;
    }
}
