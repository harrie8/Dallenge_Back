package com.example.dailychallenge.repository.challenge;

import com.example.dailychallenge.vo.challenge.ResponseChallenge;
import java.util.Optional;

public interface ChallengeRepositoryCustom {

    Optional<ResponseChallenge> searchChallengeById(Long challengeId);
}
