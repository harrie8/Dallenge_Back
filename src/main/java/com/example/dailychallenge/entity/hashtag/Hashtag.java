package com.example.dailychallenge.entity.hashtag;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Table(name = "hashtag")
@Entity
@Getter
@NoArgsConstructor
public class Hashtag {

    @Id
    @Column(name = "hashtag_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private Integer tagCount;

    @OneToMany(mappedBy = "hashtag",cascade = CascadeType.ALL)
    private List<ChallengeHashtag> challengeHashtags= new ArrayList<>();

    @Builder
    public Hashtag(String content) {
        this.content = content;
        this.tagCount = 1;
    }

    public void updateTagCount() {
        this.tagCount += 1;
    }
}
