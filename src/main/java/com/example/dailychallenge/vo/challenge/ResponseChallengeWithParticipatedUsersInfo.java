package com.example.dailychallenge.vo.challenge;

import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseChallengeWithParticipatedUsersInfo {
    private ResponseChallenge responseChallenge;
    private List<ResponseUserChallenge> responseUserChallenges;

    @Builder
    public ResponseChallengeWithParticipatedUsersInfo(ResponseChallenge responseChallenge,
                                                      List<ResponseUserChallenge> responseUserChallenges) {
        this.responseChallenge = responseChallenge;
        this.responseUserChallenges = responseUserChallenges;
    }
}
