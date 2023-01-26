package com.example.dailychallenge.service.challenge;

import com.example.dailychallenge.dto.ChallengeDto;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.repository.ChallengeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;

    public Challenge saveChallenge(ChallengeDto challengeDto) {
        Challenge challenge = challengeDto.toChallenge();

        challengeRepository.save(challenge);

        return challenge;
    }
}
