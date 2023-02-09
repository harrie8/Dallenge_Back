package com.example.dailychallenge.vo.challenge;

import com.example.dailychallenge.entity.challenge.UserChallenge;
import com.example.dailychallenge.vo.ResponseUser;
import com.querydsl.core.annotations.QueryProjection;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseUserChallenge {

    private String challengeStatus;
    private ResponseUser participatedUser;

    @Builder
    public ResponseUserChallenge(String challengeStatus, ResponseUser participatedUser) {
        this.challengeStatus = challengeStatus;
        this.participatedUser = participatedUser;
    }

    @QueryProjection
    public ResponseUserChallenge(UserChallenge userChallenge) {
        this.challengeStatus = userChallenge.getChallengeStatus().getDescription();
        this.participatedUser = ResponseUser.create(userChallenge.getUsers());
    }
}
