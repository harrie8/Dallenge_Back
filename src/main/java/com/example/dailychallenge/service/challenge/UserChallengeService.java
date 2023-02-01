package com.example.dailychallenge.service.challenge;

import com.example.dailychallenge.dto.ChallengeSearchCondition;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.ChallengeStatus;
import com.example.dailychallenge.entity.challenge.UserChallenge;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.exception.users.UserNotFound;
import com.example.dailychallenge.repository.UserChallengeRepository;
import com.example.dailychallenge.repository.UserRepository;
import com.example.dailychallenge.vo.ResponseChallenge;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UserChallengeService {

    private final UserChallengeRepository userChallengeRepository;
    private final UserRepository userRepository;

    public UserChallenge saveUserChallenge(Challenge challenge, String userEmail) {
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new UserNotFound();
        }
        UserChallenge userChallenge = UserChallenge.builder()
                .challengeStatus(ChallengeStatus.TRYING)
                .build();
        userChallenge.setUser(user);
        userChallenge.setChallenge(challenge);

        userChallengeRepository.save(userChallenge);

        return userChallenge;
    }


    public List<ResponseChallenge> searchAllSortByPopularWithPaging(Pageable pageable) {

        return userChallengeRepository.searchAllChallengesSortByPopularWithPaging(pageable);
    }

    public List<ResponseChallenge> searchByConditionSortByPopularWithPaging(ChallengeSearchCondition condition,
                                                                            Pageable pageable) {

        return userChallengeRepository.searchChallengesByConditionSortByPopularWithPaging(condition, pageable);
    }
}
