package com.example.dailychallenge.entity.challenge;

import com.example.dailychallenge.entity.User;
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
public class UserChallenge {

    @Id
    @Column(name = "user_challenge_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private ChallengeStatus challengeStatus;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @Builder
    public UserChallenge(ChallengeStatus challengeStatus, User user, Challenge challenge) {
        this.challengeStatus = challengeStatus;
        this.user = user;
        this.challenge = challenge;
    }

    public void setUser(User user) {
        if (user.getUserChallenges().contains(this)) {
            user.getUserChallenges().remove(this);
        }
        this.user = user;
        user.getUserChallenges().add(this);
    }

    public void setChallenge(Challenge challenge) {
        if (challenge.getUserChallenges().contains(this)) {
            challenge.getUserChallenges().remove(this);
        }
        this.challenge = challenge;
        challenge.getUserChallenges().add(this);
    }
}
