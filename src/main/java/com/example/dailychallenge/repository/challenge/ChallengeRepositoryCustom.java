package com.example.dailychallenge.repository.challenge;

import com.example.dailychallenge.entity.challenge.ChallengeCategory;
import com.example.dailychallenge.entity.challenge.ChallengeDuration;
import com.example.dailychallenge.entity.challenge.ChallengeLocation;
import com.example.dailychallenge.vo.challenge.ResponseChallenge;
import com.example.dailychallenge.vo.challenge.ResponseRecommendedChallenge;
import java.util.List;
import java.util.Optional;

public interface ChallengeRepositoryCustom {

    Optional<ResponseChallenge> searchChallengeById(Long challengeId);

    List<ResponseRecommendedChallenge> searchChallengesByQuestion(ChallengeCategory challengeCategory,
                                                                  ChallengeDuration challengeDuration,
                                                                  ChallengeLocation challengeLocation);
}
