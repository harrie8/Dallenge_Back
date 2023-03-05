package com.example.dailychallenge.dto;

import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.hashtag.Hashtag;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class HashtagChallengesDto implements Comparable<HashtagChallengesDto>{
    private Hashtag hashtag;
    private List<Challenge> challenges;

    @Builder
    public HashtagChallengesDto(Hashtag hashtag, List<Challenge> challenges) {
        this.hashtag = hashtag;
        this.challenges = challenges;
    }

    @Override
    public int compareTo(HashtagChallengesDto o) {
        return o.getHashtag().getTagCount() - hashtag.getTagCount();
    }
}
