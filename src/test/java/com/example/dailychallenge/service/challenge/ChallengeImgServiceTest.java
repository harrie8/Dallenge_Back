package com.example.dailychallenge.service.challenge;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.dailychallenge.dto.ChallengeDto;
import com.example.dailychallenge.dto.UserDto;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.ChallengeCategory;
import com.example.dailychallenge.entity.challenge.ChallengeDuration;
import com.example.dailychallenge.entity.challenge.ChallengeImg;
import com.example.dailychallenge.entity.challenge.ChallengeLocation;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.repository.ChallengeImgRepository;
import com.example.dailychallenge.service.users.UserService;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class ChallengeImgServiceTest {

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

    @Value("${userImgLocation}")
    private String challengeImgLocation;
    private User savedUser;
    private ChallengeDto challengeDto;

    @BeforeEach
    void beforeEach() {
        try {
            savedUser = userService.saveUser(createUser(), passwordEncoder);
            challengeDto = createChallengeDto();
        } catch (Exception e) {
            throw new IllegalArgumentException(e);
        }
    }

    public UserDto createUser() {
        UserDto userDto = new UserDto();
        userDto.setEmail("test1234@test.com");
        userDto.setUserName("홍길동");
        userDto.setInfo("testInfo");
        userDto.setPassword("1234");
        return userDto;
    }

    private ChallengeDto createChallengeDto() {
        return ChallengeDto.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .challengeCategory(ChallengeCategory.STUDY.getDescription())
                .challengeLocation(ChallengeLocation.INDOOR.getDescription())
                .challengeDuration(ChallengeDuration.WITHIN_TEN_MINUTES.getDescription())
                .build();
    }

    private List<MultipartFile> createChallengeImgFiles() {
        List<MultipartFile> result = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            String path = challengeImgLocation +"/";
            String imageName = "challengeImage" + i + ".jpg";
            result.add(new MockMultipartFile(path, imageName, "image/jpg", new byte[]{1, 2, 3, 4}));
        }
        return result;
    }

    @Test
    void updateChallengeImgs() {
        Challenge savedChallenge = challengeService.saveChallenge(challengeDto, createChallengeImgFiles(), savedUser);
        List<MultipartFile> updateChallengeImgFiles = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            String path = challengeImgLocation +"/";
            String imageName = "updatedChallengeImage" + i + ".jpg";
            updateChallengeImgFiles.add(new MockMultipartFile(path, imageName, "image/jpg", new byte[]{1, 2, 3, 4}));
        }

        challengeImgService.updateChallengeImgs(savedChallenge, updateChallengeImgFiles);

        List<ChallengeImg> all = challengeImgRepository.findAll();
        assertThat(all).extracting("oriImgName")
                .containsExactly("updatedChallengeImage0.jpg", "updatedChallengeImage1.jpg");
        assertThat(savedChallenge.getChallengeImgs()).extracting("oriImgName")
                .containsExactly("updatedChallengeImage0.jpg", "updatedChallengeImage1.jpg");
    }
}