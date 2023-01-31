package com.example.dailychallenge.repository.challenge;

import com.example.dailychallenge.dto.ChallengeSearchCondition;
import com.example.dailychallenge.vo.ResponseChallenge;
import java.util.List;
import org.springframework.data.domain.Pageable;

public interface UserChallengeRepositoryCustom {

    List<ResponseChallenge> searchAllChallengesSortByPopularWithPaging(Pageable pageable);
    List<ResponseChallenge> searchChallengesByConditionSortByPopularWithPaging(ChallengeSearchCondition condition, Pageable pageable);
}
