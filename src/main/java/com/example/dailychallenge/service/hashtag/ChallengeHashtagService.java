package com.example.dailychallenge.service.hashtag;

import com.example.dailychallenge.dto.HashtagChallengesDto;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.hashtag.ChallengeHashtag;
import com.example.dailychallenge.entity.hashtag.Hashtag;
import com.example.dailychallenge.repository.ChallengeHashtagRepository;
import com.example.dailychallenge.service.challenge.ChallengeService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChallengeHashtagService {
    private final ChallengeHashtagRepository challengeHashtagRepository;

    private final ChallengeService challengeService;
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

    public List<ChallengeHashtag> updateChallengeHashtag(Long challengeId, List<Hashtag> hashtags){
        Challenge challenge = challengeService.findById(challengeId);
        List<ChallengeHashtag> challengeHashtags = findByChallengeId(challengeId);
        List<ChallengeHashtag> res = new ArrayList<>();
        List<Hashtag> savedTag = challengeHashtags.stream() // 기존 해시태그
                .map(ChallengeHashtag::getHashtag).collect(Collectors.toList());

        for (Hashtag hashtag : hashtags) {
            if (!savedTag.contains(hashtag)) { // 새로운 태그가 있다면
                ChallengeHashtag challengeHashtag = ChallengeHashtag.builder()
                        .hashtag(hashtag)
                        .challenge(challenge)
                        .build();

                challenge.getChallengeHashtags().add(challengeHashtag);
                hashtag.getChallengeHashtags().add(challengeHashtag);
                res.add(challengeHashtag);

                challengeHashtagRepository.save(challengeHashtag);
            }
        }
        return res;
    }

    public void deleteChallengeHashtag(Long challengeId, Long hashtagId) {
        try {
            ChallengeHashtag challengeHashtag = challengeHashtagRepository
                    .findByChallengeIdAndHashtagId(challengeId, hashtagId);

            challengeHashtag.getChallenge().getChallengeHashtags().remove(challengeHashtag);
            challengeHashtagRepository.delete(challengeHashtag);
        } catch (NullPointerException e){ }
    }

    public List<ChallengeHashtag> findByChallengeId(Long challengeId){
        return challengeHashtagRepository.findAllByChallengeId(challengeId);
    }

    public List<HashtagChallengesDto> searchByHashtags(List<Hashtag> hashtags) {
        List<ChallengeHashtag> challengeHashtags = challengeHashtagRepository.searchByHashtags(hashtags);

        Map<Hashtag, List<ChallengeHashtag>> hashtagListMap = challengeHashtags.stream()
                .collect(Collectors.groupingBy(ChallengeHashtag::getHashtag));

        List<HashtagChallengesDto> hashtagChallengesDtos = new ArrayList<>();
        for (Hashtag hashtag : hashtagListMap.keySet()) {
            List<ChallengeHashtag> challengeHashtagList = hashtagListMap.get(hashtag);
            List<Challenge> challenges = challengeHashtagList.stream()
                    .map(ChallengeHashtag::getChallenge)
                    .collect(Collectors.toUnmodifiableList());
            hashtagChallengesDtos.add(HashtagChallengesDto.builder()
                    .hashtag(hashtag)
                    .challenges(challenges)
                    .build());
        }
        Collections.sort(hashtagChallengesDtos);

        return hashtagChallengesDtos;
    }
}
