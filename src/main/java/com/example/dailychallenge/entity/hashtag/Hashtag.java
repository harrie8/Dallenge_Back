package com.example.dailychallenge.entity.hashtag;

import com.example.dailychallenge.entity.BaseEntity;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Table(name = "hashtag")
@Entity
@Getter
@NoArgsConstructor
public class Hashtag extends BaseEntity {

    @Id
    @Column(name = "hashtag_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private Integer tagCount;

    @OneToMany(mappedBy = "hashtag", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
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
