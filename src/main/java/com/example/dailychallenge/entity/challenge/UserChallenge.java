package com.example.dailychallenge.entity.challenge;

import com.example.dailychallenge.entity.BaseEntity;
import com.example.dailychallenge.entity.users.User;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class UserChallenge extends BaseEntity {

    @Id
    @Column(name = "user_challenge_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private ChallengeStatus challengeStatus;

    private boolean isParticipated;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User users;

    @ManyToOne
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @Builder
    public UserChallenge(ChallengeStatus challengeStatus, User users, Challenge challenge) {
        this.challengeStatus = challengeStatus;
        this.users = users;
        this.challenge = challenge;
        this.isParticipated = false;
    }

    public void setUser(User users) {
        if (users.getUserChallenges().contains(this)) {
            users.getUserChallenges().remove(this);
        }
        this.users = users;
        users.getUserChallenges().add(this);
    }

    public void setChallenge(Challenge challenge) {
        if (challenge.getUserChallenges().contains(this)) {
            challenge.getUserChallenges().remove(this);
        }
        this.challenge = challenge;
        challenge.getUserChallenges().add(this);
    }
    public void challengeParticipate(){
        this.isParticipated = true;
    }

    public void challengeLeave(){
        this.isParticipated = false;
    }

    public void challengeSuccess() {
        this.challengeStatus = ChallengeStatus.SUCCESS;
    }
    public void challengePause() {
        this.challengeStatus = ChallengeStatus.PAUSE;
    }

    public void resetChallengeStatus(){
        this.challengeStatus = ChallengeStatus.TRYING;
    }

    public boolean isChallengeSuccess() {
        return this.challengeStatus == ChallengeStatus.SUCCESS;
    }
}
