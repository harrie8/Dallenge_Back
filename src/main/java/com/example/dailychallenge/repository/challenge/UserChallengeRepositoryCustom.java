package com.example.dailychallenge.repository.challenge;

import com.example.dailychallenge.dto.ChallengeSearchCondition;
import com.example.dailychallenge.entity.challenge.UserChallenge;
import com.example.dailychallenge.vo.challenge.ResponseChallenge;
import com.example.dailychallenge.vo.challenge.ResponseUserChallenge;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserChallengeRepositoryCustom {
    Optional<UserChallenge> findByChallengeIdAndUserId(Long challengeId, Long userId);
    List<ResponseUserChallenge> searchUserChallengeByChallengeId(Long challengeId);
    List<UserChallenge> searchUserChallengeByUserId(Long userId);
    Page<ResponseChallenge> searchAllChallenges(Pageable pageable);
    Page<ResponseChallenge> searchChallengesByCondition(ChallengeSearchCondition condition, Pageable pageable);
}
