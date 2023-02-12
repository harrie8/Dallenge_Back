package com.example.dailychallenge.repository;

import com.example.dailychallenge.entity.hashtag.ChallengeHashtag;
import com.example.dailychallenge.entity.hashtag.Hashtag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChallengeHashtagRepository extends JpaRepository<ChallengeHashtag,Long> {
    List<ChallengeHashtag> findAllByChallengeId(Long challengeId);

    List<ChallengeHashtag> findByHashtagId(Long hashtagId);

    ChallengeHashtag findByChallengeIdAndHashtagId(Long hashtagId, Long challengeId);
}
