package com.example.dailychallenge.controller.userChallenge;

import static com.example.dailychallenge.entity.challenge.ChallengeCategory.ECONOMY;
import static com.example.dailychallenge.entity.challenge.ChallengeCategory.STUDY;
import static com.example.dailychallenge.entity.challenge.ChallengeCategory.WORKOUT;
import static com.example.dailychallenge.entity.challenge.ChallengeDuration.OVER_ONE_HOUR;
import static com.example.dailychallenge.entity.challenge.ChallengeDuration.WITHIN_TEN_MINUTES;
import static com.example.dailychallenge.entity.challenge.ChallengeLocation.INDOOR;
import static com.example.dailychallenge.entity.challenge.ChallengeLocation.OUTDOOR;
import static com.example.dailychallenge.util.fixture.TokenFixture.EMAIL;
import static com.example.dailychallenge.util.fixture.TokenFixture.PASSWORD;
import static com.example.dailychallenge.util.fixture.user.UserFixture.OTHER_EMAIL;
import static com.example.dailychallenge.util.fixture.user.UserFixture.OTHER_USERNAME;
import static com.example.dailychallenge.util.fixture.user.UserFixture.USERNAME;
import static com.example.dailychallenge.util.fixture.user.UserFixture.getRequestPostProcessor;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.UserChallenge;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.service.challenge.UserChallengeService;
import com.example.dailychallenge.util.ControllerTest;
import com.example.dailychallenge.util.fixture.TestDataSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

class UserChallengeControllerTest extends ControllerTest {
    @Autowired
    private UserChallengeService userChallengeService;
    @Autowired
    private TestDataSetup testDataSetup;

    private User user;
    private Challenge challenge1;
    private User otherUser;
    private RequestPostProcessor requestPostProcessor;

    @BeforeEach
    void beforeEach() {
        user = testDataSetup.saveUser(USERNAME, EMAIL, PASSWORD);
        requestPostProcessor = getRequestPostProcessor(user);
        initData();
        otherUser = testDataSetup.saveUser(OTHER_USERNAME, OTHER_EMAIL, PASSWORD);
    }

    private void initData() {
        challenge1 = testDataSetup.챌린지를_생성한다(
                "제목입니다.1",
                "내용입니다.1",
                STUDY.getDescription(),
                INDOOR.getDescription(),
                WITHIN_TEN_MINUTES.getDescription(),
                user);
        testDataSetup.챌린지에_참가한다(challenge1, user);

        Challenge challenge2 = testDataSetup.챌린지를_생성한다(
                "제목입니다.2",
                "내용입니다.2",
                ECONOMY.getDescription(),
                OUTDOOR.getDescription(),
                OVER_ONE_HOUR.getDescription(),
                user
        );
        testDataSetup.챌린지에_참가한다(challenge2, user);

        Challenge challenge6 = null;

        for (int i = 3; i <= 10; i++) {
            Challenge challenge = testDataSetup.챌린지를_생성한다(
                    "제목입니다." + i,
                    "내용입니다." + i,
                    WORKOUT.getDescription(),
                    INDOOR.getDescription(),
                    WITHIN_TEN_MINUTES.getDescription(),
                    user
            );
            testDataSetup.챌린지에_참가한다(challenge, user);

            if (i == 6) {
                challenge6 = challenge;
            }
        }

        for (int i = 1; i <= 8; i++) {
            User user = testDataSetup.saveUser(USERNAME + i, i + "@test.com", PASSWORD);
            if (i == 1) {
                testDataSetup.챌린지에_참가한다(challenge1, user);
            }
            if (2 <= i && i <= 5) {
                testDataSetup.챌린지에_참가한다(challenge2, user);
            }
            if (i == 6) {
                testDataSetup.챌린지에_참가한다(challenge6, user);
            }
        }
    }

    @Test
    @DisplayName("챌린지 참가 테스트")
    void participateInChallengeTest() throws Exception {
        Long challenge1Id = challenge1.getId();

        mockMvc.perform(post("/challenge/{challengeId}/participate", challenge1Id)
                        .with(getRequestPostProcessor(otherUser))
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

        mockMvc.perform(delete("/challenge/{challengeId}/leave", challenge1Id)
                        .with(getRequestPostProcessor(otherUser))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("챌린지 나가기 완료!"));
    }

    @Test
    @DisplayName("챌린지 달성 완료 테스트")
    void succeedInChallengeTest() throws Exception {
        testDataSetup.saveUserBadgeEvaluation(user);
        testDataSetup.saveBadgesAndUserBadges(user);

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
        UserChallenge userChallenge = userChallengeService.succeedInChallenge(user.getId(), challenge1.getId());

        mockMvc.perform(get("/user/done")
                        .with(requestPostProcessor)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(user.getId()))
                .andExpect(jsonPath("$[0].challengeId").value(challenge1.getId()))
                .andExpect(jsonPath("$[0].challengeTitle").value(challenge1.getTitle()))
                .andExpect(jsonPath("$[0].challengeContent").value(challenge1.getContent()))
                .andExpect(jsonPath("$[0].challengeStatus").value(userChallenge.getChallengeStatus().toString()))
                .andExpect(jsonPath("$[0].createdAt").isNotEmpty());
    }
}