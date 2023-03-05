package com.example.dailychallenge.repository.challenge;

import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.hashtag.Hashtag;
import com.example.dailychallenge.vo.challenge.ResponseChallenge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ChallengeRepositoryCustom {

    Optional<ResponseChallenge> searchChallengeById(Long challengeId);

    Page<ResponseChallenge> searchChallengeByHashtag(String content, Pageable pageable);
}
