package com.example.dailychallenge.controller.badge;

import static com.example.dailychallenge.entity.challenge.ChallengeCategory.STUDY;
import static com.example.dailychallenge.entity.challenge.ChallengeDuration.WITHIN_TEN_MINUTES;
import static com.example.dailychallenge.entity.challenge.ChallengeLocation.INDOOR;
import static com.example.dailychallenge.util.fixture.TokenFixture.AUTHORIZATION;
import static com.example.dailychallenge.util.fixture.TokenFixture.EMAIL;
import static com.example.dailychallenge.util.fixture.TokenFixture.PASSWORD;
import static com.example.dailychallenge.util.fixture.challenge.ChallengeImgFixture.createChallengeImgFiles;
import static com.example.dailychallenge.util.fixture.user.UserFixture.OTHER_EMAIL;
import static com.example.dailychallenge.util.fixture.user.UserFixture.OTHER_USERNAME;
import static com.example.dailychallenge.util.fixture.user.UserFixture.USERNAME;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.dailychallenge.dto.CommentDto;
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
    private String token;

    private void initData() {
        user = testDataSetup.saveUser(USERNAME, EMAIL, PASSWORD);
        testDataSetup.saveUserBadgeEvaluation(user);
        testDataSetup.saveBadgesAndUserBadges(user);
        token = generateToken(user);
    }

    @Test
    @DisplayName("챌린지 10개 생성 뱃지 테스트")
    void createChallengeCreateBadgeTest() throws Exception {
        initData();

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
                                fieldWithPath("badgeInfo.createBadgeName").description("챌린지 생성 뱃지 이름"),
                                fieldWithPath("badgeInfo.badgeImgUrl").description("챌린지 생성 뱃지 이미지 경로")
                        )
                ));
    }

    @Test
    @DisplayName("챌린지 10개 달성 완료 테스트")
    void createAchievementBadgeTest() throws Exception {
        initData();

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

        mockMvc.perform(post("/challenge/{challengeId}/success", challenge.getId())
                        .header(AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("challengeId").description("내가 달성한 챌린지 ID")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("badgeInfo.createBadgeName").description("챌린지 N개 달성 뱃지 이름"),
                                fieldWithPath("badgeInfo.badgeImgUrl").description("챌린지 N개 달성 뱃지 이미지 경로")
                        )
                ));
    }

    private MockMultipartFile createMultipartFiles() {
        String path = "commentDtoImg";
        String imageName = "commentDtoImg.jpg";
        return new MockMultipartFile(path, imageName,
                "image/jpg", new byte[]{1, 2, 3, 4});
    }

    @Test
    @DisplayName("후기 10개 작성 뱃지 테스트")
    void createCommentWriteBadgeTest() throws Exception {
        initData();

        User otherUser = testDataSetup.saveUser(OTHER_USERNAME, OTHER_EMAIL, PASSWORD);
        for (int i = 1; i <= 9; i++) {
            Challenge challenge = testDataSetup.챌린지를_생성한다(
                    "제목입니다." + i,
                    "내용입니다." + i,
                    STUDY.getDescription(),
                    INDOOR.getDescription(),
                    WITHIN_TEN_MINUTES.getDescription(),
                    user);
            testDataSetup.챌린지에_참가한다(challenge, otherUser);
            testDataSetup.챌린지에_참가한다(challenge, user);
            testDataSetup.챌린지에_댓글을_단다(challenge, user, "content" + i);

            testDataSetup.후기_작성_뱃지를_만들_수_있으면_만든다(user);
        }
        Challenge challenge = testDataSetup.챌린지를_생성한다(
                "제목입니다." + 10,
                "내용입니다." + 10,
                STUDY.getDescription(),
                INDOOR.getDescription(),
                WITHIN_TEN_MINUTES.getDescription(),
                user);
        testDataSetup.챌린지에_참가한다(challenge, otherUser);
        testDataSetup.챌린지에_참가한다(challenge, user);
        CommentDto commentDto = CommentDto.builder()
                .content("댓글 내용")
                .build();
        String json = objectMapper.writeValueAsString(commentDto);
        MockMultipartFile mockCommentDto = new MockMultipartFile("commentDto",
                "commentDto",
                "application/json", json.getBytes(UTF_8));

        Long challengeId = challenge.getId();
        mockMvc.perform(multipart("/{challengeId}/comment/new", challengeId)
                        .file(mockCommentDto)
                        .part(new MockPart("commentImgFiles", "commentImgFiles", createMultipartFiles().getBytes()))
                        .part(new MockPart("commentImgFiles", "commentImgFiles", createMultipartFiles().getBytes()))
                        .header(AUTHORIZATION, token)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("challengeId").description("후기를 남기려는 챌린지 ID")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("badgeInfo.createBadgeName").description("후기 N개 작성 뱃지 이름"),
                                fieldWithPath("badgeInfo.badgeImgUrl").description("후기 N개 작성 뱃지 이미지 경로")
                        )
                ));
    }

    @Test
    @DisplayName("모든 뱃지 조회 테스트")
    void getAllBadgesTest() throws Exception {
        mockMvc.perform(get("/badges")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        relaxedResponseFields(
                                fieldWithPath("[].badgeName").description("뱃지 이름"),
                                fieldWithPath("[].badgeStatus").description("뱃지 획득 여부 - true: 획득, false: 획득 못함"),
                                fieldWithPath("[].badgeImgUrl").description("뱃지 이미지 url - ipAddress:port번호/badgeImage/challengeCreate/challengeCreate10.svg로 요청하면 뱃지 이미지가 출력됩니다.")
                        )
                ));
    }
}