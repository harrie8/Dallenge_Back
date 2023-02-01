package com.example.dailychallenge.repository.challenge;

import com.example.dailychallenge.dto.ChallengeSearchCondition;
import com.example.dailychallenge.vo.ResponseChallenge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserChallengeRepositoryCustom {

    Page<ResponseChallenge> searchAllChallengesSortByPopularWithPaging(Pageable pageable);
    Page<ResponseChallenge> searchChallengesByConditionSortByPopularWithPaging(ChallengeSearchCondition condition, Pageable pageable);
}
