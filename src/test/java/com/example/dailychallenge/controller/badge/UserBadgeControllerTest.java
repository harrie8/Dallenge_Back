package com.example.dailychallenge.controller.badge;

import static com.example.dailychallenge.entity.challenge.ChallengeCategory.STUDY;
import static com.example.dailychallenge.entity.challenge.ChallengeDuration.WITHIN_TEN_MINUTES;
import static com.example.dailychallenge.entity.challenge.ChallengeLocation.INDOOR;
import static com.example.dailychallenge.util.fixture.TokenFixture.EMAIL;
import static com.example.dailychallenge.util.fixture.TokenFixture.PASSWORD;
import static com.example.dailychallenge.util.fixture.user.UserFixture.USERNAME;
import static com.example.dailychallenge.util.fixture.user.UserFixture.getRequestPostProcessor;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.UserChallenge;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.util.ControllerTest;
import com.example.dailychallenge.util.fixture.TestDataSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

class UserBadgeControllerTest extends ControllerTest {

    @Autowired
    private TestDataSetup testDataSetup;

    private User user;
    private RequestPostProcessor requestPostProcessor;

    @BeforeEach
    void beforeEach() {
        user = testDataSetup.saveUser(USERNAME, EMAIL, PASSWORD);
        requestPostProcessor = getRequestPostProcessor(user);
    }

    private void 챌린지를_생성하고_참가하고_달성한다(int index) {
        for (int i = 1; i <= index; i++) {
            Challenge challenge = testDataSetup.챌린지를_생성한다(
                    "제목입니다." + i,
                    "내용입니다." + i,
                    STUDY.getDescription(),
                    INDOOR.getDescription(),
                    WITHIN_TEN_MINUTES.getDescription(),
                    user);
            UserChallenge userChallenge = testDataSetup.챌린지에_참가한다(challenge, user);
            testDataSetup.챌린지_생성_뱃지를_만들_수_있으면_만든다(user);

            testDataSetup.챌린지를_달성한다(userChallenge);
            testDataSetup.챌린지_달성_뱃지를_만들_수_있으면_만든다(user);
        }
    }

    @Test
    @DisplayName("유저의 모든 뱃지들을 조회하는 테스트")
    void findAllUserBadgesTest() throws Exception {
        testDataSetup.saveUserBadgeEvaluation(user);
        testDataSetup.saveBadgesAndUserBadges(user);

        챌린지를_생성하고_참가하고_달성한다(20);

        mockMvc.perform(get("/user/badges")
                        .with(requestPostProcessor) // 토큰 인증 처리, 입력한 정보로 인증된 사용자 생성
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].badgeName").value("챌린지 10개 생성"))
                .andExpect(jsonPath("$[0].badgeStatus").value(true))
                .andExpect(jsonPath("$[0].badgeImgUrl").value("badgeImage/challengeCreate/challengeCreate10.svg"))
                .andExpect(jsonPath("$[1].badgeName").value("챌린지 15개 생성"))
                .andExpect(jsonPath("$[1].badgeStatus").value(true))
                .andExpect(jsonPath("$[1].badgeImgUrl").value("badgeImage/challengeCreate/challengeCreate15.svg"))
                .andExpect(jsonPath("$[2].badgeName").value("챌린지 20개 생성"))
                .andExpect(jsonPath("$[2].badgeStatus").value(true))
                .andExpect(jsonPath("$[2].badgeImgUrl").value("badgeImage/challengeCreate/challengeCreate20.svg"))
                .andExpect(jsonPath("$[3].badgeName").value("챌린지 25개 생성"))
                .andExpect(jsonPath("$[3].badgeStatus").value(false))
                .andExpect(jsonPath("$[3].badgeImgUrl").value("badgeImage/challengeCreate/challengeCreate25.svg"))
                .andExpect(jsonPath("$[4].badgeName").value("챌린지 30개 생성"))
                .andExpect(jsonPath("$[4].badgeStatus").value(false))
                .andExpect(jsonPath("$[4].badgeImgUrl").value("badgeImage/challengeCreate/challengeCreate30.svg"));
    }
}