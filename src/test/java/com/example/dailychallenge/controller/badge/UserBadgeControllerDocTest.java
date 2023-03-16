package com.example.dailychallenge.controller.badge;

import static com.example.dailychallenge.entity.challenge.ChallengeCategory.STUDY;
import static com.example.dailychallenge.entity.challenge.ChallengeDuration.WITHIN_TEN_MINUTES;
import static com.example.dailychallenge.entity.challenge.ChallengeLocation.INDOOR;
import static com.example.dailychallenge.util.fixture.TokenFixture.AUTHORIZATION;
import static com.example.dailychallenge.util.fixture.TokenFixture.EMAIL;
import static com.example.dailychallenge.util.fixture.TokenFixture.PASSWORD;
import static com.example.dailychallenge.util.fixture.user.UserFixture.USERNAME;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.UserChallenge;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.util.RestDocsTest;
import com.example.dailychallenge.util.fixture.TestDataSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

public class UserBadgeControllerDocTest extends RestDocsTest {
    @Autowired
    private TestDataSetup testDataSetup;

    private User user;
    private String token;

    @BeforeEach
    void beforeEach() {
        user = testDataSetup.saveUser(USERNAME, EMAIL, PASSWORD);
        token = generateToken(user);
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
                        .header(AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("[].badgeName").description("뱃지 이름"),
                                fieldWithPath("[].badgeStatus").description("뱃지 획득 여부 - true: 획득, false: 획득 못함"),
                                fieldWithPath("[].badgeImgUrl").description("뱃지 이미지 url - ipAddress:port번호/badgeImage/challengeCreate/challengeCreate10.svg로 요청하면 뱃지 이미지가 출력됩니다.")
                        )
                ));
    }
}