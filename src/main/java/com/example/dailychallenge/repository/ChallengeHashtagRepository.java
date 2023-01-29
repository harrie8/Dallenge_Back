package com.example.dailychallenge.repository;

import com.example.dailychallenge.entity.hashtag.ChallengeHashtag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChallengeHashtagRepository extends JpaRepository<ChallengeHashtag,Long> {
}
