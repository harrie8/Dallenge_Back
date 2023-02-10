package com.example.dailychallenge.service.challenge;

import com.example.dailychallenge.dto.ChallengeSearchCondition;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.ChallengeStatus;
import com.example.dailychallenge.entity.challenge.UserChallenge;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.exception.userChallenge.UserChallengeDuplicate;
import com.example.dailychallenge.repository.UserChallengeRepository;
import com.example.dailychallenge.vo.challenge.ResponseChallenge;
import com.example.dailychallenge.vo.challenge.ResponseUserChallenge;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserChallengeService {

    private final UserChallengeRepository userChallengeRepository;

    public UserChallenge saveUserChallenge(Challenge challenge, User user) {
        checkDuplicate(challenge, user);

        UserChallenge userChallenge = UserChallenge.builder()
                .challengeStatus(ChallengeStatus.TRYING)
                .build();
        userChallenge.setUser(user);
        userChallenge.setChallenge(challenge);

        userChallengeRepository.save(userChallenge);

        return userChallenge;
    }

    private void checkDuplicate(Challenge challenge, User user) {
        userChallengeRepository.findByChallengeIdAndUserId(challenge.getId(), user.getId())
                .ifPresent(userChallenge -> {
                    throw new UserChallengeDuplicate();
                });
    }

    public List<ResponseUserChallenge> searchByChallengeId(Long challengeId) {
        return userChallengeRepository.searchUserChallengeByChallengeId(challengeId);
    }

    public Page<ResponseChallenge> searchAll(Pageable pageable) {

        return userChallengeRepository.searchAllChallenges(pageable);
    }

    public Page<ResponseChallenge> searchByCondition(ChallengeSearchCondition condition,
                                                     Pageable pageable) {

        return userChallengeRepository.searchChallengesByCondition(condition, pageable);
    }
}
