package com.example.dailychallenge.controller.userChallenge;

import static com.example.dailychallenge.util.fixture.ChallengeImgFixture.createChallengeImgFiles;
import static com.example.dailychallenge.util.fixture.UserFixture.createOtherUser;
import static com.example.dailychallenge.util.fixture.UserFixture.createSpecificUserDto;
import static com.example.dailychallenge.util.fixture.UserFixture.createUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.dailychallenge.dto.ChallengeDto;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.ChallengeCategory;
import com.example.dailychallenge.entity.challenge.ChallengeDuration;
import com.example.dailychallenge.entity.challenge.ChallengeLocation;
import com.example.dailychallenge.entity.challenge.UserChallenge;
import com.example.dailychallenge.entity.comment.Comment;
import com.example.dailychallenge.entity.hashtag.ChallengeHashtag;
import com.example.dailychallenge.entity.hashtag.Hashtag;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.repository.ChallengeHashtagRepository;
import com.example.dailychallenge.repository.CommentRepository;
import com.example.dailychallenge.repository.HashtagRepository;
import com.example.dailychallenge.service.challenge.ChallengeService;
import com.example.dailychallenge.service.challenge.UserChallengeService;
import com.example.dailychallenge.service.users.UserService;
import com.example.dailychallenge.util.ControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

class UserChallengeControllerTest extends ControllerTest {
    @Autowired
    private UserService userService;
    @Autowired
    private ChallengeService challengeService;
    @Autowired
    private UserChallengeService userChallengeService;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private HashtagRepository hashtagRepository;
    @Autowired
    private ChallengeHashtagRepository challengeHashtagRepository;

    private User savedUser;
    private Challenge challenge1;
    private User otherUser;
    private RequestPostProcessor requestPostProcessor;

    @BeforeEach
    void beforeEach() throws Exception {
        initData();
        requestPostProcessor = user(userService.loadUserByUsername(savedUser.getEmail()));
    }

    private void initData() throws Exception {
        savedUser = userService.saveUser(createUser(), passwordEncoder);

        ChallengeDto challengeDto1 = ChallengeDto.builder()
                .title("제목입니다.1")
                .content("내용입니다.1")
                .challengeCategory(ChallengeCategory.STUDY.getDescription())
                .challengeLocation(ChallengeLocation.INDOOR.getDescription())
                .challengeDuration(ChallengeDuration.WITHIN_TEN_MINUTES.getDescription())
                .build();
        challenge1 = challengeService.saveChallenge(challengeDto1, createChallengeImgFiles(), savedUser);

        userChallengeService.saveUserChallenge(challenge1, savedUser);

        Comment comment = Comment.builder()
                .content("content")
                .build();
        comment.saveCommentChallenge(challenge1);
        commentRepository.save(comment);

        Hashtag hashtag = Hashtag.builder()
                .content("content")
                .build();
        hashtagRepository.save(hashtag);

        ChallengeHashtag challengeHashtag = ChallengeHashtag.builder()
                .hashtag(hashtag)
                .challenge(challenge1)
                .build();
        challenge1.getChallengeHashtags().add(challengeHashtag);
        hashtag.getChallengeHashtags().add(challengeHashtag);
        challengeHashtagRepository.save(challengeHashtag);

        ChallengeDto challengeDto2 = ChallengeDto.builder()
                .title("제목입니다.2")
                .content("내용입니다.2")
                .challengeCategory(ChallengeCategory.ECONOMY.getDescription())
                .challengeLocation(ChallengeLocation.OUTDOOR.getDescription())
                .challengeDuration(ChallengeDuration.OVER_ONE_HOUR.getDescription())
                .build();
        Challenge challenge2 = challengeService.saveChallenge(challengeDto2, createChallengeImgFiles(), savedUser);

        userChallengeService.saveUserChallenge(challenge2, savedUser);

        Challenge challenge6 = null;

        for (int i = 3; i <= 10; i++) {
            ChallengeDto challengeDto = ChallengeDto.builder()
                    .title("제목입니다." + i)
                    .content("내용입니다." + i)
                    .challengeCategory(ChallengeCategory.WORKOUT.getDescription())
                    .challengeLocation(ChallengeLocation.INDOOR.getDescription())
                    .challengeDuration(ChallengeDuration.WITHIN_TEN_MINUTES.getDescription())
                    .build();
            Challenge challenge = challengeService.saveChallenge(challengeDto, createChallengeImgFiles(), savedUser);

            userChallengeService.saveUserChallenge(challenge, savedUser);

            if (i == 6) {
                challenge6 = challenge;
            }
        }

        for (int i = 1; i <= 8; i++) {
            User user = userService.saveUser(
                    createSpecificUserDto("홍길동" + i, i + "@test.com"), passwordEncoder);
            if (i == 1) {
                userChallengeService.saveUserChallenge(challenge1, user);
            }
            if (2 <= i && i <= 5) {
                userChallengeService.saveUserChallenge(challenge2, user);
            }

            if (i == 6) {
                userChallengeService.saveUserChallenge(challenge6, user);
            }
        }

        otherUser = userService.saveUser(createOtherUser(), passwordEncoder);
    }

    @Test
    @DisplayName("챌린지 참가 테스트")
    void participateInChallengeTest() throws Exception {
        Long challenge1Id = challenge1.getId();
        RequestPostProcessor otherRequestPostProcessor = user(userService.loadUserByUsername(otherUser.getEmail()));

        mockMvc.perform(post("/challenge/{challengeId}/participate", challenge1Id)
                        .with(otherRequestPostProcessor)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.message").value("챌린지 참가 완료!"));
    }

    @Test
    @DisplayName("챌린지 나가기 테스트")
    void leaveChallengeTest() throws Exception {
        userChallengeService.saveUserChallenge(challenge1, otherUser);
        Long challenge1Id = challenge1.getId();
        RequestPostProcessor otherRequestPostProcessor = user(userService.loadUserByUsername(otherUser.getEmail()));

        mockMvc.perform(delete("/challenge/{challengeId}/leave", challenge1Id)
                        .with(otherRequestPostProcessor)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("챌린지 나가기 완료!"));
    }

    @Test
    @DisplayName("챌린지 달성 완료 테스트")
    void succeedInChallengeTest() throws Exception {
        mockMvc.perform(post("/challenge/{challengeId}/success", challenge1.getId())
                        .with(requestPostProcessor)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("챌린지 달성 완료!"));
    }

    @Test
    @DisplayName("챌린지 중지 완료 테스트")
    void pauseChallengeTest() throws Exception {
        mockMvc.perform(post("/challenge/{challengeId}/pause", challenge1.getId())
                        .with(requestPostProcessor)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("챌린지 중지 완료!"));
    }

    @Test
    @DisplayName("오늘 수행(성공)한 챌린지 조회 테스트")
    void getTodayUserChallengeTest() throws Exception {
        UserChallenge userChallenge = userChallengeService.succeedInChallenge(savedUser.getId(), challenge1.getId());

        mockMvc.perform(get("/user/done")
                        .with(requestPostProcessor)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].challengeId").value(challenge1.getId()))
                .andExpect(jsonPath("$[0].challengeTitle").value(challenge1.getTitle()))
                .andExpect(jsonPath("$[0].challengeContent").value(challenge1.getContent()))
                .andExpect(jsonPath("$[0].challengeStatus").value(userChallenge.getChallengeStatus().toString()));
    }
}