package com.example.dailychallenge.service.challenge;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.dailychallenge.dto.UserDto;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.ChallengeCategory;
import com.example.dailychallenge.entity.challenge.ChallengeDuration;
import com.example.dailychallenge.entity.challenge.ChallengeLocation;
import com.example.dailychallenge.entity.challenge.ChallengeStatus;
import com.example.dailychallenge.entity.challenge.UserChallenge;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.repository.UserRepository;
import com.example.dailychallenge.service.users.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class UserChallengeServiceTest {

    @Autowired
    private UserChallengeService userChallengeService;
    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void beforeEach() {
        userRepository.deleteAll();
    }

    public UserDto createUser() {
        UserDto userDto = new UserDto();
        userDto.setEmail("test1234@test.com");
        userDto.setUserName("홍길동");
        userDto.setInfo("testInfo");
        userDto.setPassword("1234");
        return userDto;
    }

    @Test
    @DisplayName("유저 챌린지 생성 테스트 - 연관관계 테스트")
    void createUserChallenge() throws Exception {
        User savedUser = userService.saveUser(createUser(), passwordEncoder);
        Challenge challenge = Challenge.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .challengeCategory(ChallengeCategory.STUDY)
                .challengeLocation(ChallengeLocation.INDOOR)
                .challengeDuration(ChallengeDuration.WITHIN_TEN_MINUTES)
                .build();

        UserChallenge userChallenge = userChallengeService.saveUserChallenge(challenge, savedUser);

        assertEquals(ChallengeStatus.TRYING, userChallenge.getChallengeStatus());
        assertEquals(challenge, userChallenge.getChallenge());
        assertEquals(savedUser, userChallenge.getUsers());
    }
}
