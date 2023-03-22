package com.example.dailychallenge.entity.challenge;

import com.example.dailychallenge.entity.BaseEntity;
import com.example.dailychallenge.entity.users.User;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
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
    private static final Boolean NOT_ACHIEVED = false;
    private static final Boolean ACHIEVED = true;
    private static final String DELIMITER = ",";

    @Id
    @Column(name = "user_challenge_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false)
    private ChallengeStatus challengeStatus;

    private boolean isParticipated;

    @Column(nullable = false)
    private String weeklyAchievement;

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
        this.weeklyAchievement = "false,false,false,false,false,false,false"; // 월요일부터 일요일 순서
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

    public boolean isChallengePause() {
        return this.challengeStatus == ChallengeStatus.PAUSE;
    }

    public List<Boolean> converWeeklyChallengeToList() {
       return new ArrayList<>(Arrays.stream(weeklyAchievement.split(","))
                .map(Boolean::valueOf)
                .collect(Collectors.toUnmodifiableList()));
    }

    public void updateWeeklyAchievement(LocalDate date) {
        int dayNumber = date.getDayOfWeek().getValue() - 1; // 월요일 0, 일요일 6
        List<Boolean> week = converWeeklyChallengeToList();
        if (week.get(dayNumber) == NOT_ACHIEVED) {
            week.set(dayNumber, ACHIEVED);
        }
        weeklyAchievement = week.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    public void updateWeeklyAchievementToFalse(LocalDate date) {
        int dayNumber = date.getDayOfWeek().getValue() - 1; // 월요일 0, 일요일 6
        List<Boolean> week = converWeeklyChallengeToList();
        if (week.get(dayNumber) == ACHIEVED) {
            week.set(dayNumber, NOT_ACHIEVED);
        }
        weeklyAchievement = week.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(","));
    }

    public void resetWeeklyAchievement(){
        this.weeklyAchievement = "false,false,false,false,false,false,false";
    }
}
