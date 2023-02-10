package com.example.dailychallenge.service.challenge;

import static com.example.dailychallenge.util.fixture.ChallengeFixture.createChallengeDto;
import static com.example.dailychallenge.util.fixture.ChallengeImgFixture.createChallengeImgFiles;
import static com.example.dailychallenge.util.fixture.UserFixture.createUser;
import static org.assertj.core.api.Assertions.assertThat;

import com.example.dailychallenge.dto.ChallengeDto;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.ChallengeImg;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.repository.ChallengeImgRepository;
import com.example.dailychallenge.service.users.UserService;
import com.example.dailychallenge.util.ServiceTest;
import com.example.dailychallenge.util.fixture.ChallengeImgFixture;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

class ChallengeImgServiceTest extends ServiceTest {

    @Autowired
    private ChallengeService challengeService;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ChallengeImgService challengeImgService;
    @Autowired
    private ChallengeImgRepository challengeImgRepository;

    private User savedUser;
    private ChallengeDto challengeDto;

    @BeforeEach
    void beforeEach() throws Exception {
        savedUser = userService.saveUser(createUser(), passwordEncoder);
        challengeDto = createChallengeDto();
    }

    @Test
    void updateChallengeImgs() {
        List<MultipartFile> createChallengeImgFiles = createChallengeImgFiles();
        Challenge savedChallenge = challengeService.saveChallenge(challengeDto, createChallengeImgFiles, savedUser);
        List<MultipartFile> updateChallengeImgFiles = ChallengeImgFixture.updateChallengeImgFiles();

        challengeImgService.updateChallengeImgs(savedChallenge, updateChallengeImgFiles);

        List<ChallengeImg> all = challengeImgRepository.findAll();
        assertThat(all).extracting("oriImgName")
                .containsExactly("updatedChallengeImage0.jpg", "updatedChallengeImage1.jpg");
        assertThat(savedChallenge.getChallengeImgs()).extracting("oriImgName")
                .containsExactly("updatedChallengeImage0.jpg", "updatedChallengeImage1.jpg");
    }
}