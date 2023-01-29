package com.example.dailychallenge.entity.hashtag;

import com.example.dailychallenge.entity.challenge.Challenge;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
public class ChallengeHashtag {

    @Id
    @Column(name = "challenge_hashtag_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "challenge_id")
    private Challenge challenge;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hashtag_id")
    private Hashtag hashtag;

    @Builder
    public ChallengeHashtag(Challenge challenge, Hashtag hashtag) {
        this.challenge = challenge;
        this.hashtag = hashtag;
    }
}
