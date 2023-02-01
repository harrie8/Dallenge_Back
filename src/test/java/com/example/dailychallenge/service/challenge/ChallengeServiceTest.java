package com.example.dailychallenge.service.challenge;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.example.dailychallenge.dto.ChallengeDto;
import com.example.dailychallenge.dto.UserDto;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.ChallengeCategory;
import com.example.dailychallenge.entity.challenge.ChallengeDuration;
import com.example.dailychallenge.entity.challenge.ChallengeLocation;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.exception.challenge.ChallengeCategoryNotFound;
import com.example.dailychallenge.service.users.UserService;
import org.junit.jupiter.api.DisplayName;
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
public class ChallengeServiceTest {

    @Autowired
    private ChallengeService challengeService;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Value("${userImgLocation}")
    private String challengeImgLocation;

    public UserDto createUser() {
        UserDto userDto = new UserDto();
        userDto.setEmail("test1234@test.com");
        userDto.setUserName("홍길동");
        userDto.setInfo("testInfo");
        userDto.setPassword("1234");
        return userDto;
    }

    MultipartFile createMultipartFiles() {
        String path = challengeImgLocation +"/";
        String imageName = "challengeImage.jpg";
        return new MockMultipartFile(path, imageName,
                "image/jpg", new byte[]{1, 2, 3, 4});
    }

    @Test
    @DisplayName("챌린지 생성 테스트")
    void createChallenge() throws Exception {
        User savedUser = userService.saveUser(createUser(), passwordEncoder);
        ChallengeDto challengeDto = ChallengeDto.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .challengeCategory(ChallengeCategory.STUDY.getDescription())
                .challengeLocation(ChallengeLocation.INDOOR.getDescription())
                .challengeDuration(ChallengeDuration.WITHIN_TEN_MINUTES.getDescription())
                .build();
        MultipartFile challengeImg = createMultipartFiles();

        Challenge challenge = challengeService.saveChallenge(challengeDto, challengeImg, savedUser);

        assertEquals(challengeDto.getTitle(), challenge.getTitle());
        assertEquals(challengeDto.getContent(), challenge.getContent());
        assertEquals(challengeDto.getChallengeCategory(), challenge.getChallengeCategory().getDescription());
        assertEquals(challengeDto.getChallengeLocation(), challenge.getChallengeLocation().getDescription());
        assertEquals(challengeDto.getChallengeDuration(), challenge.getChallengeDuration().getDescription());
        assertEquals(savedUser, challenge.getUsers());
    }

    @Test
    @DisplayName("존재하지 않는 카테고리로 챌린지 생성 테스트")
    void createChallengeByCategoryNotFound() throws Exception {
        User savedUser = userService.saveUser(createUser(), passwordEncoder);
        ChallengeDto challengeDto = ChallengeDto.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .challengeCategory(ChallengeCategory.STUDY.getDescription() + "error")
                .challengeLocation(ChallengeLocation.INDOOR.getDescription())
                .challengeDuration(ChallengeDuration.WITHIN_TEN_MINUTES.getDescription())
                .build();
        MultipartFile challengeImg = createMultipartFiles();

        Throwable exception = assertThrows(ChallengeCategoryNotFound.class, () -> {
            challengeService.saveChallenge(challengeDto, challengeImg, savedUser);
        });
        assertEquals("존재하지 않는 챌린지 카테고리입니다.", exception.getMessage());
    }
}
