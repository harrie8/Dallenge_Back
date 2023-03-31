package com.example.dailychallenge.entity.badge;

import com.example.dailychallenge.entity.BaseEntity;
import com.example.dailychallenge.entity.users.User;

import javax.persistence.*;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user_badge")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserBadge extends BaseEntity {
    @Id
    @Column(name = "user_badge_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private Boolean status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User users;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "badge_id")
    private Badge badge;

    @Builder
    public UserBadge(Boolean status, User users, Badge badge) {
        this.status = status;
        setUser(users);
        setBadge(badge);
    }

    public void setUser(User users) {
        if (users.getUserBadges().contains(this)) {
            users.getUserBadges().remove(this);
        }
        this.users = users;
        users.getUserBadges().add(this);
    }

    public void setBadge(Badge badge) {
        if (badge.getUserBadges().contains(this)) {
            badge.getUserBadges().remove(this);
        }
        this.badge = badge;
        badge.getUserBadges().add(this);
    }

    public void setStatusToTrue() {
        this.status = true;
    }
}
