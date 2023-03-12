package com.example.dailychallenge.controller.badge;

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
import static com.example.dailychallenge.util.fixture.challenge.ChallengeImgFixture.createChallengeImgFiles;
import static com.example.dailychallenge.util.fixture.user.UserFixture.USERNAME;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.dailychallenge.dto.HashtagDto;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.ChallengeCategory;
import com.example.dailychallenge.entity.challenge.ChallengeDuration;
import com.example.dailychallenge.entity.challenge.ChallengeLocation;
import com.example.dailychallenge.entity.challenge.UserChallenge;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.util.RestDocsTest;
import com.example.dailychallenge.util.fixture.TestDataSetup;
import com.example.dailychallenge.vo.challenge.RequestCreateChallenge;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.web.multipart.MultipartFile;

public class BadgeControllerDocTest extends RestDocsTest {
    @Autowired
    private TestDataSetup testDataSetup;

    private User user;
    private Challenge challenge1;
    private String token;

    @BeforeEach
    void beforeEach() {
        user = testDataSetup.saveUser(USERNAME, EMAIL, PASSWORD);
        token = generateToken(user);
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
    @DisplayName("챌린지 10개 생성 뱃지 테스트")
    void createChallengeCreateBadgeTest() throws Exception {
        testDataSetup.saveUserBadgeEvaluation(user);

        for (int i = 1; i <= 9; i++) {
            Challenge challenge = testDataSetup.챌린지를_생성한다(
                    "제목입니다." + i,
                    "내용입니다." + i,
                    STUDY.getDescription(),
                    INDOOR.getDescription(),
                    WITHIN_TEN_MINUTES.getDescription(),
                    user);
            testDataSetup.챌린지에_참가한다(challenge, user);
            testDataSetup.챌린지_생성_뱃지를_만들_수_있으면_만든다(user);
        }

        RequestCreateChallenge requestCreateChallenge = RequestCreateChallenge.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .challengeCategory("공부")
                .challengeLocation("실내")
                .challengeDuration("10분 이내")
                .build();
        List<MultipartFile> challengeImgFiles = createChallengeImgFiles();

        String json = objectMapper.writeValueAsString(requestCreateChallenge);
        MockMultipartFile mockRequestCreateChallenge = new MockMultipartFile("requestCreateChallenge",
                "requestCreateChallenge",
                "application/json", json.getBytes(
                StandardCharsets.UTF_8));

        HashtagDto hashtagDto = HashtagDto.builder()
                .content(List.of("tag1", "tag2"))
                .build();
        String hashtagDtoJson = objectMapper.writeValueAsString(hashtagDto);
        MockMultipartFile mockHashtagDto = new MockMultipartFile("hashtagDto",
                "hashtagDto",
                "application/json", hashtagDtoJson.getBytes(UTF_8));

        String challengeCategoryDescriptions = String.join(", ", ChallengeCategory.getDescriptions());
        String challengeLocationDescriptions = String.join(", ", ChallengeLocation.getDescriptions());
        String challengeDurationDescriptions = String.join(", ", ChallengeDuration.getDescriptions());
        mockMvc.perform(multipart("/challenge/new")
                        .file(mockRequestCreateChallenge)
                        .part(new MockPart("challengeImgFiles", "challengeImgFile", challengeImgFiles.get(0).getBytes()))
                        .part(new MockPart("challengeImgFiles", "challengeImgFile", challengeImgFiles.get(1).getBytes()))
                        .part(new MockPart("challengeImgFiles", "challengeImgFile", challengeImgFiles.get(2).getBytes()))
                        .file(mockHashtagDto)
                        .header(AUTHORIZATION, token)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(restDocs.document(
                        requestParts(
                                partWithName("requestCreateChallenge").description("챌린지 데이터(JSON)")
                                        .attributes(key("type").value("JSON")),
                                partWithName("challengeImgFiles").description("챌린지 이미지 파일들(FILE)").optional()
                                        .attributes(key("type").value(".jpg")),
                                partWithName("hashtagDto").description("해시태그 데이터(JSON)").optional()
                                        .attributes(key("type").value("JSON"))
                        ),
                        requestPartFields("requestCreateChallenge",
                                fieldWithPath("title").description("제목"),
                                fieldWithPath("content").description("내용"),
                                fieldWithPath("challengeCategory").description("카테고리")
                                        .attributes(key("format").value(
                                                challengeCategoryDescriptions)),
                                fieldWithPath("challengeLocation").description("장소")
                                        .attributes(key("format").value(
                                                challengeLocationDescriptions)),
                                fieldWithPath("challengeDuration").description("기간")
                                        .attributes(key("format").value(
                                                challengeDurationDescriptions))
                        ),
                        requestPartFields("hashtagDto",
                                fieldWithPath("content").description("해시태그 내용")
                                        .attributes(key("format").value("\"\", \" \"은 허용하지 않습니다."))
                        ),
                        relaxedResponseFields(
                                fieldWithPath("createBadgeName").description("생성된 뱃지 이름 ")
                        )
                ));
    }

    @Test
    @DisplayName("챌린지 10개 달성 완료 테스트")
    void createAchievementBadgeTest() throws Exception {
        initData();
        testDataSetup.saveUserBadgeEvaluation(user);
        for (int i = 1; i <= 9; i++) {
            Challenge challenge = testDataSetup.챌린지를_생성한다(
                    "제목입니다." + i,
                    "내용입니다." + i,
                    STUDY.getDescription(),
                    INDOOR.getDescription(),
                    WITHIN_TEN_MINUTES.getDescription(),
                    user);
            UserChallenge userChallenge = testDataSetup.챌린지에_참가한다(challenge, user);
            testDataSetup.챌린지를_달성한다(userChallenge);
            testDataSetup.챌린지_달성_뱃지를_만들_수_있으면_만든다(user);
        }
        Challenge challenge = testDataSetup.챌린지를_생성한다(
                "제목입니다." + 10,
                "내용입니다." + 10,
                STUDY.getDescription(),
                INDOOR.getDescription(),
                WITHIN_TEN_MINUTES.getDescription(),
                user);
        testDataSetup.챌린지에_참가한다(challenge, user);

        mockMvc.perform(post("/challenge/{challengeId}/success", challenge1.getId())
                        .header(AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("챌린지 달성 완료!"))
                .andExpect(jsonPath("$.createBadgeName").value("챌린지 10개 달성"))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("challengeId").description("내가 달성한 챌린지 ID")
                        ),
                        responseFields(
                                fieldWithPath("code").description("HTTP STATUS"),
                                fieldWithPath("message").description("메시지"),
                                fieldWithPath("createBadgeName").description("챌린지 N개 달성 뱃지 이름")
                        )
                ));
    }
}