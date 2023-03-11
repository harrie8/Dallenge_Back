package com.example.dailychallenge.controller.userChallenge;

import static com.example.dailychallenge.entity.challenge.ChallengeCategory.ECONOMY;
import static com.example.dailychallenge.entity.challenge.ChallengeCategory.STUDY;
import static com.example.dailychallenge.entity.challenge.ChallengeCategory.WORKOUT;
import static com.example.dailychallenge.entity.challenge.ChallengeDuration.OVER_ONE_HOUR;
import static com.example.dailychallenge.entity.challenge.ChallengeDuration.WITHIN_TEN_MINUTES;
import static com.example.dailychallenge.entity.challenge.ChallengeLocation.INDOOR;
import static com.example.dailychallenge.entity.challenge.ChallengeLocation.OUTDOOR;
import static com.example.dailychallenge.util.fixture.TokenFixture.AUTHORIZATION;
import static com.example.dailychallenge.util.fixture.TokenFixture.EMAIL;
import static com.example.dailychallenge.util.fixture.TokenFixture.PASSWORD;
import static com.example.dailychallenge.util.fixture.user.UserFixture.OTHER_EMAIL;
import static com.example.dailychallenge.util.fixture.user.UserFixture.OTHER_USERNAME;
import static com.example.dailychallenge.util.fixture.user.UserFixture.USERNAME;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.UserChallenge;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.service.challenge.UserChallengeService;
import com.example.dailychallenge.util.RestDocsTest;
import com.example.dailychallenge.util.fixture.TestDataSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;

public class UserChallengeControllerDocTest extends RestDocsTest {
    @Autowired
    private UserChallengeService userChallengeService;
    @Autowired
    private TestDataSetup testDataSetup;

    private User user;
    private String token;
    private Challenge challenge1;
    private User otherUser;

    @BeforeEach
    void beforeEach() {
        user = testDataSetup.saveUser(USERNAME, EMAIL, PASSWORD);
        token = generateToken(user);
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
    void participateInChallenge() throws Exception {
        Long challenge1Id = challenge1.getId();

        mockMvc.perform(post("/challenge/{challengeId}/participate", challenge1Id)
                        .header(AUTHORIZATION, generateToken(otherUser))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(201))
                .andExpect(jsonPath("$.message").value("챌린지 참가 완료!"))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("challengeId").description("참가하고 싶은 챌린지 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").description("HTTP STATUS"),
                                fieldWithPath("message").description("메시지")
                        )
                ));
    }

    @Test
    @DisplayName("챌린지 나가기 테스트")
    void leaveChallengeTest() throws Exception {
        userChallengeService.saveUserChallenge(challenge1, otherUser);
        Long challenge1Id = challenge1.getId();

        mockMvc.perform(delete("/challenge/{challengeId}/leave", challenge1Id)
                        .header(AUTHORIZATION, generateToken(otherUser))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("챌린지 나가기 완료!"))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("challengeId").description("나가고 싶은 챌린지 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").description("HTTP STATUS"),
                                fieldWithPath("message").description("메시지")
                        )
                ));
    }

    @Test
    @DisplayName("챌린지 달성 완료 테스트")
    void succeedInChallengeTest() throws Exception {

        mockMvc.perform(post("/challenge/{challengeId}/success", challenge1.getId())
                        .header(AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("챌린지 달성 완료!"))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("challengeId").description("내가 달성한 챌린지 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").description("HTTP STATUS"),
                                fieldWithPath("message").description("메시지")
                        )
                ));
    }

    @Test
    @DisplayName("챌린지 중지 완료 테스트")
    void pauseChallengeTest() throws Exception {

        mockMvc.perform(post("/challenge/{challengeId}/pause", challenge1.getId())
                        .header(AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("챌린지 중지 완료!"))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("challengeId").description("내가 달성한 챌린지 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").description("HTTP STATUS"),
                                fieldWithPath("message").description("메시지")
                        )
                ));
    }

    @Test
    @DisplayName("오늘 수행(성공)한 챌린지 조회 테스트")
    void getTodayUserChallengeTest() throws Exception {
        UserChallenge userChallenge = userChallengeService.succeedInChallenge(user.getId(), challenge1.getId());

        mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/user/done")
                        .header(AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].challengeId").value(challenge1.getId()))
                .andExpect(jsonPath("$[0].challengeTitle").value(challenge1.getTitle()))
                .andExpect(jsonPath("$[0].challengeContent").value(challenge1.getContent()))
                .andExpect(jsonPath("$[0].challengeStatus").value(userChallenge.getChallengeStatus().toString()))
                .andDo(restDocs.document(
                        responseFields(
                                fieldWithPath("[].userId").description("유저 ID"),
                                fieldWithPath("[].challengeId").description("성공한 챌린지 ID"),
                                fieldWithPath("[].challengeTitle").description("성공한 챌린지 제목"),
                                fieldWithPath("[].challengeContent").description("성공한 챌린지 내용"),
                                fieldWithPath("[].challengeStatus").description("성공한 챌린지 상태"),
                                fieldWithPath("[].createdAt").description("챌린지 생성 시간")
                        )
                ));
    }
}

