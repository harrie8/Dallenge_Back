package com.example.dailychallenge.service.hashtag;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.dailychallenge.dto.ChallengeDto;
import com.example.dailychallenge.dto.HashtagChallengesDto;
import com.example.dailychallenge.dto.UserDto;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.ChallengeCategory;
import com.example.dailychallenge.entity.challenge.ChallengeDuration;
import com.example.dailychallenge.entity.challenge.ChallengeLocation;
import com.example.dailychallenge.entity.hashtag.ChallengeHashtag;
import com.example.dailychallenge.entity.hashtag.Hashtag;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.service.challenge.ChallengeService;
import com.example.dailychallenge.service.users.UserService;
import java.util.List;
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
class ChallengeHashtagServiceTest {

    @Autowired
    private ChallengeService challengeService;
    @Value("${userImgLocation}")
    private String challengeImgLocation;
    @Autowired
    private HashtagService hashtagService;
    @Autowired
    private ChallengeHashtagService challengeHashtagService;
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

    MultipartFile createMultipartFiles() {
        String path = challengeImgLocation +"/";
        String imageName = "challengeImage.jpg";
        MockMultipartFile multipartFile = new MockMultipartFile(path, imageName,
                "image/jpg", new byte[]{1, 2, 3, 4});
        return multipartFile;
    }

    public Challenge createChallenge() {
        User savedUser = userService.saveUser(createUser(), passwordEncoder);
        ChallengeDto challengeDto = ChallengeDto.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .challengeCategory(ChallengeCategory.STUDY.getDescription())
                .challengeLocation(ChallengeLocation.INDOOR.getDescription())
                .challengeDuration(ChallengeDuration.WITHIN_TEN_MINUTES.getDescription())
                .build();
        MultipartFile challengeImg = createMultipartFiles();
        List<MultipartFile> challengeImgFiles = List.of(challengeImg);

        return challengeService.saveChallenge(challengeDto, challengeImgFiles, savedUser);
    }

    public User saveUser() {
        return userService.saveUser(createUser(), passwordEncoder);
    }

    private Challenge createChallenge(String title, User user) {
        ChallengeDto challengeDto = ChallengeDto.builder()
                .title(title)
                .content("내용입니다.")
                .challengeCategory(ChallengeCategory.STUDY.getDescription())
                .challengeLocation(ChallengeLocation.INDOOR.getDescription())
                .challengeDuration(ChallengeDuration.WITHIN_TEN_MINUTES.getDescription())
                .build();
        MultipartFile challengeImg = createMultipartFiles();
        List<MultipartFile> challengeImgFiles = List.of(challengeImg);

        return challengeService.saveChallenge(challengeDto, challengeImgFiles, user);
    }

    public List<Hashtag> createHashtag() {
        List<String> hashtagDto = List.of("tag1");
        return hashtagService.saveHashtag(hashtagDto);
    }

    @Test
    @DisplayName("챌린지 해시태그 생성 테스트 - 연관관계 테스트")
    void createUserChallenge() {
        Challenge challenge = createChallenge();
        List<Hashtag> hashtag = createHashtag();

        List<ChallengeHashtag> challengeHashtags = challengeHashtagService.saveChallengeHashtag(challenge, hashtag);

        assertEquals(challengeHashtags.get(0).getChallenge(),challenge);
        assertEquals(challengeHashtags.get(0).getHashtag(),hashtag.get(0));
    }

    @Test
    @DisplayName("챌린지 해시태그 수정 테스트")
    void updateUserChallenge() {
        Challenge challenge = createChallenge();
        List<Hashtag> hashtag = createHashtag();
        challengeHashtagService.saveChallengeHashtag(challenge, hashtag);
        Long challengeId = challenge.getId();
        List<String> updateHashtagDto = List.of("editTag1", "editTag2");
        List<Hashtag> updateHashTag = hashtagService.updateHashtag(updateHashtagDto, challengeId);

        List<ChallengeHashtag> updateChallengeHashtag = challengeHashtagService.updateChallengeHashtag(challengeId,
                updateHashTag);

        assertThat(updateChallengeHashtag).extracting("challenge").containsOnly(challenge);
        assertThat(updateChallengeHashtag).extracting("hashtag").containsExactlyElementsOf(updateHashTag);
    }

    @Test
    void searchByHashtagTest() {
        User user = saveUser();
        Challenge challenge = createChallenge("제목입니다.1", user);
        List<String> hashtagDto = List.of("tag1", "tag2");
        List<Hashtag> hashtags = hashtagService.saveHashtag(hashtagDto);
        challengeHashtagService.saveChallengeHashtag(challenge, hashtags);

        Challenge challenge2 = createChallenge("제목입니다.2", user);
        List<String> hashtagDto2 = List.of("tag1", "tag2", "tag3");
        List<Hashtag> hashtags2 = hashtagService.saveHashtag(hashtagDto2);
        challengeHashtagService.saveChallengeHashtag(challenge2, hashtags2);

        Challenge challenge3 = createChallenge("제목입니다.3", user);
        List<String> hashtagDto3 = List.of("tag2", "tag4");
        List<Hashtag> hashtags3 = hashtagService.saveHashtag(hashtagDto3);
        challengeHashtagService.saveChallengeHashtag(challenge3, hashtags3);

        List<HashtagChallengesDto> results = challengeHashtagService.searchByHashtags(hashtags2);

        assertEquals(3, results.size());

        // hashtag2
        assertEquals(hashtags2.get(1), results.get(0).getHashtag());
        assertTrue(results.get(0).getChallenges().containsAll(List.of(challenge, challenge2, challenge3)));

        // hashtag1
        assertEquals(hashtags2.get(0), results.get(1).getHashtag());
        assertTrue(results.get(1).getChallenges().containsAll(List.of(challenge, challenge2)));

        // hashtag3
        assertEquals(hashtags2.get(2), results.get(2).getHashtag());
        assertTrue(results.get(2).getChallenges().contains(challenge2));
    }
}