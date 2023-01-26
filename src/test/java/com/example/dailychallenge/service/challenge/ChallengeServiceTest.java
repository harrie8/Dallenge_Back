package com.example.dailychallenge.service.challenge;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.dailychallenge.dto.ChallengeDto;
import com.example.dailychallenge.dto.UserDto;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.ChallengeCategory;
import com.example.dailychallenge.entity.challenge.ChallengeDuration;
import com.example.dailychallenge.entity.challenge.ChallengeLocation;
import com.example.dailychallenge.service.UserService;
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
public class ChallengeServiceTest {

    @Autowired
    private ChallengeService challengeService;
    @Autowired
    private UserService userService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public UserDto createUser() {
        UserDto userDto = new UserDto();
        userDto.setEmail("test1234@test.com");
        userDto.setUserName("홍길동");
        userDto.setInfo("testInfo");
        userDto.setPassword("1234");
        return userDto;
    }

    @Test
    @DisplayName("챌린지 생성 테스트")
    void createChallenge() throws Exception {
        UserDto userDto = createUser();
        userService.saveUser(userDto, passwordEncoder);
        ChallengeDto challengeDto = ChallengeDto.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .challengeCategory(ChallengeCategory.STUDY.getDescription())
                .challengeLocation(ChallengeLocation.INDOOR.getDescription())
                .challengeDuration(ChallengeDuration.WITHIN_TEN_MINUTES.getDescription())
                .build();

        Challenge challenge = challengeService.saveChallenge(challengeDto);

        assertEquals(challengeDto.getTitle(), challenge.getTitle());
        assertEquals(challengeDto.getContent(), challenge.getContent());
        assertEquals(challengeDto.getChallengeCategory(), challenge.getChallengeCategory().getDescription());
        assertEquals(challengeDto.getChallengeLocation(), challenge.getChallengeLocation().getDescription());
        assertEquals(challengeDto.getChallengeDuration(), challenge.getChallengeDuration().getDescription());
    }
}
