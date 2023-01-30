package com.example.dailychallenge.service.challenge;

import com.example.dailychallenge.entity.challenge.ChallengeImg;
import com.example.dailychallenge.repository.ChallengeImgRepository;
import com.example.dailychallenge.service.FileService;
import com.querydsl.core.util.StringUtils;
import javax.persistence.EntityNotFoundException;
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
        challengeImg.getChallenge().setChallengeImg(challengeImg);

    }

    public void updateChallengeImg(Long challengeImgId, MultipartFile userImgFile) {
        if(!userImgFile.isEmpty()){
            ChallengeImg savedChallengeImg = challengeImgRepository.findById(challengeImgId)
                    .orElseThrow(EntityNotFoundException::new);
            if(!StringUtils.isNullOrEmpty(savedChallengeImg.getImgName())){
                fileService.deleteFile(savedChallengeImg.getImgName());
            }

            String oriImgName = userImgFile.getOriginalFilename();
            String imgName = fileService.uploadFile(userImgFile);
            String imgUrl = "/images/"+imgName;
            savedChallengeImg.updateUserImg(oriImgName,imgName,imgUrl);
        }
    }

}
