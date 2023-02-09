package com.example.dailychallenge.service.challenge;

import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.ChallengeImg;
import com.example.dailychallenge.repository.ChallengeImgRepository;
import com.example.dailychallenge.service.FileService;
import com.querydsl.core.util.StringUtils;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


@Service
@RequiredArgsConstructor
@Transactional
public class ChallengeImgService {

    private final ChallengeImgRepository challengeImgRepository;
    private final FileService fileService;

    public void saveChallengeImg(ChallengeImg challengeImg, MultipartFile challengeImgFile) {
        String oriImgName = challengeImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        // 파일 업로드
        if (!StringUtils.isNullOrEmpty(oriImgName)) {
            imgName = fileService.uploadFile(challengeImgFile);
            imgUrl = "/images/"+imgName;
        }

        // 이미지 정보 저장
        challengeImg.updateUserImg(oriImgName,imgName,imgUrl);
        challengeImgRepository.save(challengeImg);
        challengeImg.getChallenge().addChallengeImg(challengeImg);

    }

    public void updateChallengeImgs(Challenge challenge, List<MultipartFile> updateChallengeImgFiles) {
        challenge.clearChallengeImgs();
        Long challengeId = challenge.getId();
        challengeImgRepository.deleteChallengeImgsByChallengeId(challengeId);

        for (MultipartFile updateChallengeImgFile : updateChallengeImgFiles) {
            ChallengeImg updateChallengeImg = new ChallengeImg();
            updateChallengeImg.setChallenge(challenge);
            saveChallengeImg(updateChallengeImg, updateChallengeImgFile);
        }
    }

}
