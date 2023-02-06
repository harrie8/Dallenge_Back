package com.example.dailychallenge.service.challenge;

import com.example.dailychallenge.dto.ChallengeDto;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.ChallengeImg;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.exception.challenge.ChallengeNotFound;
import com.example.dailychallenge.repository.ChallengeRepository;
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

    public Challenge saveChallenge(ChallengeDto challengeDto, MultipartFile challengeImgFile, User user) {
        Challenge challenge = challengeDto.toChallenge();
        challenge.setUser(user);

        challengeRepository.save(challenge);

        if (!challengeImgFile.isEmpty()) {
            ChallengeImg challengeImg = new ChallengeImg();
            challengeImg.setChallenge(challenge);
            challengeImgService.saveChallengeImg(challengeImg, challengeImgFile);
        }

        return challenge;
    }

    // TODO: 2023-02-06 댓글, 해시태그 정보도 같이 반환하기
    public Challenge findById(Long id) {
        return challengeRepository.findById(id).orElseThrow(ChallengeNotFound::new);
    }
}
