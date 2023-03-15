package com.example.dailychallenge.entity.badge;

import com.example.dailychallenge.entity.BaseEntity;
import com.example.dailychallenge.entity.users.User;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_badge_evaluation")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserBadgeEvaluation extends BaseEntity {
    @Id
    @Column(name = "user_badge_evaluation_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private Integer numberOfAchievement;
    @Column(nullable = false)
    private Integer numberOfChallengeCreate;
    @Column(nullable = false)
    private Integer numberOfCommentWrite;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User users;

    @Builder
    public UserBadgeEvaluation(User users) {
        setUser(users);
        numberOfAchievement = 0;
        numberOfChallengeCreate = 0;
        numberOfCommentWrite = 0;
    }

    public void setUser(User users) {
        users.saveUserBadgeEvaluation(this);
        this.users = users;
    }

    public void addNumberOfAchievement() {
        this.numberOfAchievement++;
    }
    public void addNumberOfChallengeCreate() {
        this.numberOfChallengeCreate++;
    }

    public void addNumberOfCommentWrite() {
        this.numberOfCommentWrite++;
    }

    public void subtractNumberOfAchievement() {
        this.numberOfAchievement--;
    }

    public void subtractNumberOfChallengeCreate() {
        this.numberOfChallengeCreate--;
    }
}
