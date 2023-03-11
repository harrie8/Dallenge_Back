package com.example.dailychallenge.util.fixture.challenge;

import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.ChallengeImg;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class ChallengeImgFixture {

    @Value("${userImgLocation}")
    private static String challengeImgLocation;

    public static List<MultipartFile> createChallengeImgFiles() {
        List<MultipartFile> result = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            String path = challengeImgLocation +"/";
            String imageName = "challengeImage" + i + ".jpg";
            result.add(new MockMultipartFile(path, imageName, "image/jpg", new byte[]{1, 2, 3, 4}));
        }
        return result;
    }

    public static List<MultipartFile> updateChallengeImgFiles() {
        List<MultipartFile> updateChallengeImgFiles = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            String path = challengeImgLocation +"/";
            String imageName = "updatedChallengeImage" + i + ".jpg";
            updateChallengeImgFiles.add(new MockMultipartFile(path, imageName, "image/jpg", new byte[]{1, 2, 3, 4}));
        }
        return updateChallengeImgFiles;
    }

    public static List<ChallengeImg> createSpecificChallengeImgs(String imgUrl, String imgName, String oriImgName,
                                                                 Challenge challenge, int repeatCount) {
        List<ChallengeImg> challengeImgs = new ArrayList<>();
        for (int i = 0; i < repeatCount; i++) {
            ChallengeImg challengeImg = new ChallengeImg();
            challengeImg.setImgUrl(imgUrl);
            challengeImg.setImgName(imgName);
            challengeImg.setOriImgName(oriImgName);
            challengeImg.setChallenge(challenge);
            challengeImg.getChallenge().addChallengeImg(challengeImg);
            challengeImgs.add(challengeImg);
        }
        return challengeImgs;
    }
}
