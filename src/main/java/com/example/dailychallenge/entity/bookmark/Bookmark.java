package com.example.dailychallenge.entity.bookmark;

import com.example.dailychallenge.entity.BaseEntity;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.users.User;
import javax.persistence.Column;
import javax.persistence.Entity;
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
public class Bookmark extends BaseEntity {

    @Id
    @Column(name = "bookmark_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User users;

    @ManyToOne
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @Builder
    public Bookmark(User users, Challenge challenge) {
        this.users = users;
        this.challenge = challenge;
    }

    public void saveBookmarkUser(User user) {
        this.users = user;
    }

    public void saveBookmarkChallenge(Challenge challenge) {
        this.challenge = challenge;
    }
}
