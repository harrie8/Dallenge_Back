package com.example.dailychallenge.service.hashtag;

import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.hashtag.ChallengeHashtag;
import com.example.dailychallenge.entity.hashtag.Hashtag;
import com.example.dailychallenge.repository.ChallengeHashtagRepository;
import com.example.dailychallenge.service.challenge.ChallengeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ChallengeHashtagService {
    private final ChallengeHashtagRepository challengeHashtagRepository;
    public List<ChallengeHashtag> saveChallengeHashtag(Challenge challenge, List<Hashtag> hashtags) {

        List<ChallengeHashtag> challengeHashtags = new ArrayList<>();

        for (Hashtag hashtag : hashtags) {
            ChallengeHashtag challengeHashtag = ChallengeHashtag.builder()
                    .hashtag(hashtag)
                    .challenge(challenge)
                    .build();

            challenge.getChallengeHashtags().add(challengeHashtag);
            hashtag.getChallengeHashtags().add(challengeHashtag);
            challengeHashtags.add(challengeHashtag);

            challengeHashtagRepository.save(challengeHashtag);
        }
        return challengeHashtags;
    }
}
