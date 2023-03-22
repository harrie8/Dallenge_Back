package com.example.dailychallenge.controller.badge;

import static com.example.dailychallenge.entity.challenge.ChallengeCategory.STUDY;
import static com.example.dailychallenge.entity.challenge.ChallengeDuration.WITHIN_TEN_MINUTES;
import static com.example.dailychallenge.entity.challenge.ChallengeLocation.INDOOR;
import static com.example.dailychallenge.util.fixture.TokenFixture.EMAIL;
import static com.example.dailychallenge.util.fixture.TokenFixture.PASSWORD;
import static com.example.dailychallenge.util.fixture.challenge.ChallengeImgFixture.createChallengeImgFiles;
import static com.example.dailychallenge.util.fixture.user.UserFixture.OTHER_EMAIL;
import static com.example.dailychallenge.util.fixture.user.UserFixture.OTHER_USERNAME;
import static com.example.dailychallenge.util.fixture.user.UserFixture.USERNAME;
import static com.example.dailychallenge.util.fixture.user.UserFixture.getRequestPostProcessor;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.dailychallenge.dto.CommentDto;
import com.example.dailychallenge.dto.HashtagDto;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.ChallengeStatus;
import com.example.dailychallenge.entity.challenge.UserChallenge;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.util.ControllerTest;
import com.example.dailychallenge.util.fixture.TestDataSetup;
import com.example.dailychallenge.vo.challenge.RequestCreateChallenge;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.web.multipart.MultipartFile;

class BadgeControllerTest extends ControllerTest {
    @Value("${defaultBadgeImgLocation}")
    private String badgeImgLocation;
    @Value("${badgeImgFileExtension}")
    private String badgeImgFileExtension;

    @Autowired
    private TestDataSetup testDataSetup;

    private User user;
    private RequestPostProcessor requestPostProcessor;

    private void initData() {
        user = testDataSetup.saveUser(USERNAME, EMAIL, PASSWORD);
        testDataSetup.saveUserBadgeEvaluation(user);
        testDataSetup.saveBadgesAndUserBadges(user);
        requestPostProcessor = getRequestPostProcessor(user);
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
                "application/json", json.getBytes(UTF_8));

        HashtagDto hashtagDto = HashtagDto.builder()
                .content(List.of("tag1", "tag2"))
                .build();
        String hashtagDtoJson = objectMapper.writeValueAsString(hashtagDto);
        MockMultipartFile mockHashtagDto = new MockMultipartFile("hashtagDto",
                "hashtagDto",
                "application/json", hashtagDtoJson.getBytes(UTF_8));

        mockMvc.perform(multipart("/challenge/new")
                        .file(mockRequestCreateChallenge)
                        .part(new MockPart("challengeImgFiles", "challengeImgFile", challengeImgFiles.get(0).getBytes()))
                        .part(new MockPart("challengeImgFiles", "challengeImgFile", challengeImgFiles.get(1).getBytes()))
                        .part(new MockPart("challengeImgFiles", "challengeImgFile", challengeImgFiles.get(2).getBytes()))
                        .file(mockHashtagDto)
                        .with(requestPostProcessor) // 토큰 인증 처리, 입력한 정보로 인증된 사용자 생성
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(requestCreateChallenge.getTitle()))
                .andExpect(jsonPath("$.content").value(requestCreateChallenge.getContent()))
                .andExpect(jsonPath("$.challengeCategory").value(requestCreateChallenge.getChallengeCategory()))
                .andExpect(jsonPath("$.challengeLocation").value(requestCreateChallenge.getChallengeLocation()))
                .andExpect(jsonPath("$.challengeDuration").value(requestCreateChallenge.getChallengeDuration()))
                .andExpect(jsonPath("$.challengeStatus").value(ChallengeStatus.TRYING.getDescription()))
                .andExpect(jsonPath("$.challengeImgUrls[*]", hasItem(startsWith("/images/"))))
                .andExpect(jsonPath("$.challengeHashtags[*]", contains("tag1", "tag2")))
                .andExpect(jsonPath("$.challengeOwnerUser.userName").value(user.getUserName()))
                .andExpect(jsonPath("$.challengeOwnerUser.email").value(user.getEmail()))
                .andExpect(jsonPath("$.challengeOwnerUser.userId").value(user.getId()))
                .andExpect(jsonPath("$.badgeInfo.createBadgeName").value("챌린지 10개 생성"))
                .andExpect(jsonPath("$.badgeInfo.badgeImgUrl").value(
                        badgeImgLocation + "challengeCreate/challengeCreate10" + badgeImgFileExtension));
    }

    @Test
    @DisplayName("챌린지 10개 달성 뱃지 테스트")
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
                        .with(requestPostProcessor)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("챌린지 달성 완료!"))
                .andExpect(jsonPath("$.badgeInfo.createBadgeName").value("챌린지 10개 달성"))
                .andExpect(jsonPath("$.badgeInfo.badgeImgUrl").value(
                        badgeImgLocation + "achievement/achievement10" + badgeImgFileExtension));
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
                        .with(requestPostProcessor)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.content").value("댓글 내용"))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.userId").value(user.getId()))
                .andExpect(jsonPath("$.badgeInfo.createBadgeName").value("후기 10개 작성"))
                .andExpect(jsonPath("$.badgeInfo.badgeImgUrl").value(
                        badgeImgLocation + "write/comment10" + badgeImgFileExtension));
    }

    @Test
    @DisplayName("모든 뱃지 조회 테스트")
    void getAllBadgesTest() throws Exception {
        mockMvc.perform(get("/badges")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].badgeName").value("챌린지 10개 달성"))
                .andExpect(jsonPath("$[0].badgeStatus").value(false))
                .andExpect(jsonPath("$[0].badgeImgUrl").value("badgeImage/achievement/achievement10.svg"))
                .andExpect(jsonPath("$[1].badgeName").value("챌린지 20개 달성"))
                .andExpect(jsonPath("$[1].badgeStatus").value(false))
                .andExpect(jsonPath("$[1].badgeImgUrl").value("badgeImage/achievement/achievement20.svg"))
                .andExpect(jsonPath("$[2].badgeName").value("챌린지 30개 달성"))
                .andExpect(jsonPath("$[2].badgeStatus").value(false))
                .andExpect(jsonPath("$[2].badgeImgUrl").value("badgeImage/achievement/achievement30.svg"))
                .andExpect(jsonPath("$[3].badgeName").value("챌린지 40개 달성"))
                .andExpect(jsonPath("$[3].badgeStatus").value(false))
                .andExpect(jsonPath("$[3].badgeImgUrl").value("badgeImage/achievement/achievement40.svg"))
                .andExpect(jsonPath("$[4].badgeName").value("챌린지 50개 달성"))
                .andExpect(jsonPath("$[4].badgeStatus").value(false))
                .andExpect(jsonPath("$[4].badgeImgUrl").value("badgeImage/achievement/achievement50.svg"))
                .andExpect(jsonPath("$[5].badgeName").value("후기 10개 작성"))
                .andExpect(jsonPath("$[5].badgeStatus").value(false))
                .andExpect(jsonPath("$[5].badgeImgUrl").value("badgeImage/write/comment10.svg"))
                .andExpect(jsonPath("$[6].badgeName").value("후기 20개 작성"))
                .andExpect(jsonPath("$[6].badgeStatus").value(false))
                .andExpect(jsonPath("$[6].badgeImgUrl").value("badgeImage/write/comment20.svg"))
                .andExpect(jsonPath("$[7].badgeName").value("후기 30개 작성"))
                .andExpect(jsonPath("$[7].badgeStatus").value(false))
                .andExpect(jsonPath("$[7].badgeImgUrl").value("badgeImage/write/comment30.svg"))
                .andExpect(jsonPath("$[8].badgeName").value("후기 40개 작성"))
                .andExpect(jsonPath("$[8].badgeStatus").value(false))
                .andExpect(jsonPath("$[8].badgeImgUrl").value("badgeImage/write/comment40.svg"))
                .andExpect(jsonPath("$[9].badgeName").value("후기 50개 작성"))
                .andExpect(jsonPath("$[9].badgeStatus").value(false))
                .andExpect(jsonPath("$[9].badgeImgUrl").value("badgeImage/write/comment50.svg"))
                .andExpect(jsonPath("$[10].badgeName").value("챌린지 10개 생성"))
                .andExpect(jsonPath("$[10].badgeStatus").value(false))
                .andExpect(jsonPath("$[10].badgeImgUrl").value("badgeImage/challengeCreate/challengeCreate10.svg"))
                .andExpect(jsonPath("$[11].badgeName").value("챌린지 15개 생성"))
                .andExpect(jsonPath("$[11].badgeStatus").value(false))
                .andExpect(jsonPath("$[11].badgeImgUrl").value("badgeImage/challengeCreate/challengeCreate15.svg"))
                .andExpect(jsonPath("$[12].badgeName").value("챌린지 20개 생성"))
                .andExpect(jsonPath("$[12].badgeStatus").value(false))
                .andExpect(jsonPath("$[12].badgeImgUrl").value("badgeImage/challengeCreate/challengeCreate20.svg"))
                .andExpect(jsonPath("$[13].badgeName").value("챌린지 25개 생성"))
                .andExpect(jsonPath("$[13].badgeStatus").value(false))
                .andExpect(jsonPath("$[13].badgeImgUrl").value("badgeImage/challengeCreate/challengeCreate25.svg"))
                .andExpect(jsonPath("$[14].badgeName").value("챌린지 30개 생성"))
                .andExpect(jsonPath("$[14].badgeStatus").value(false))
                .andExpect(jsonPath("$[14].badgeImgUrl").value("badgeImage/challengeCreate/challengeCreate30.svg"));
    }
}