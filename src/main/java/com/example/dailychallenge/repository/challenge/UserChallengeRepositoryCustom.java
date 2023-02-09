package com.example.dailychallenge.repository.challenge;

import com.example.dailychallenge.dto.ChallengeSearchCondition;
import com.example.dailychallenge.vo.challenge.ResponseChallenge;
import com.example.dailychallenge.vo.challenge.ResponseUserChallenge;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserChallengeRepositoryCustom {
    List<ResponseUserChallenge> searchUserChallengeByChallengeId(Long challengeId);
    Page<ResponseChallenge> searchAllChallenges(Pageable pageable);
    Page<ResponseChallenge> searchChallengesByCondition(ChallengeSearchCondition condition, Pageable pageable);
}
