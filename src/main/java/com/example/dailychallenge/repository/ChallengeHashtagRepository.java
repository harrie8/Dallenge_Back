package com.example.dailychallenge.repository;

import com.example.dailychallenge.entity.hashtag.ChallengeHashtag;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeHashtagRepository extends JpaRepository<ChallengeHashtag,Long>, ChallengeHashtagRepositoryCustom {
    List<ChallengeHashtag> findAllByChallengeId(Long challengeId);

    List<ChallengeHashtag> findByHashtagId(Long hashtagId);

    ChallengeHashtag findByChallengeIdAndHashtagId(Long challengeId, Long hashtagId);
}
