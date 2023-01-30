package com.example.dailychallenge.service.challenge;

import com.example.dailychallenge.dto.ChallengeDto;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.ChallengeImg;
import com.example.dailychallenge.repository.ChallengeRepository;
import javax.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@Transactional
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final ChallengeImgService challengeImgService;

    public Challenge saveChallenge(ChallengeDto challengeDto, MultipartFile challengeImgFile) {
        Challenge challenge = challengeDto.toChallenge();

        challengeRepository.save(challenge);

        ChallengeImg challengeImg = new ChallengeImg();
        challengeImg.setChallenge(challenge);
        challengeImgService.saveChallengeImg(challengeImg, challengeImgFile);

        return challenge;
    }

    public Challenge findById(Long id) {
        return challengeRepository.findById(id).orElseThrow(EntityNotFoundException::new);
    }
}
