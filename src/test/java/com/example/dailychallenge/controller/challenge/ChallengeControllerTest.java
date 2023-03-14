package com.example.dailychallenge.controller.challenge;

import static com.example.dailychallenge.entity.challenge.ChallengeCategory.ECONOMY;
import static com.example.dailychallenge.entity.challenge.ChallengeCategory.STUDY;
import static com.example.dailychallenge.entity.challenge.ChallengeCategory.WORKOUT;
import static com.example.dailychallenge.entity.challenge.ChallengeDuration.OVER_ONE_HOUR;
import static com.example.dailychallenge.entity.challenge.ChallengeDuration.WITHIN_TEN_MINUTES;
import static com.example.dailychallenge.entity.challenge.ChallengeLocation.INDOOR;
import static com.example.dailychallenge.entity.challenge.ChallengeLocation.OUTDOOR;
import static com.example.dailychallenge.util.fixture.TokenFixture.EMAIL;
import static com.example.dailychallenge.util.fixture.TokenFixture.PASSWORD;
import static com.example.dailychallenge.util.fixture.challenge.ChallengeImgFixture.createChallengeImgFiles;
import static com.example.dailychallenge.util.fixture.challenge.ChallengeImgFixture.updateChallengeImgFiles;
import static com.example.dailychallenge.util.fixture.user.UserFixture.USERNAME;
import static com.example.dailychallenge.util.fixture.user.UserFixture.getRequestPostProcessor;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.dailychallenge.dto.ChallengeSearchCondition;
import com.example.dailychallenge.dto.HashtagDto;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.ChallengeStatus;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.util.ControllerTest;
import com.example.dailychallenge.util.fixture.TestDataSetup;
import com.example.dailychallenge.vo.challenge.RequestCreateChallenge;
import com.example.dailychallenge.vo.challenge.RequestUpdateChallenge;
import java.util.List;
import java.util.stream.Stream;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.web.multipart.MultipartFile;

class ChallengeControllerTest extends ControllerTest {
    @Autowired
    private TestDataSetup testDataSetup;

    private User user;
    private RequestPostProcessor requestPostProcessor;
    private Challenge challenge1;

    @BeforeEach
    void beforeEach() {
        user = testDataSetup.saveUser(USERNAME, EMAIL, PASSWORD);
        requestPostProcessor = getRequestPostProcessor(user);
    }

    private void initChallengeData() {
        challenge1 = testDataSetup.챌린지를_생성한다(
                "제목입니다.1",
                "내용입니다.1",
                STUDY.getDescription(),
                INDOOR.getDescription(),
                WITHIN_TEN_MINUTES.getDescription(),
                user);
        testDataSetup.챌린지에_참가한다(challenge1, user);
        testDataSetup.챌린지예_댓글을_단다(challenge1, user);
        testDataSetup.챌린지에_해시태그를_단다(challenge1, List.of("tag1", "tag2"));
    }

    private void initData() {
        initChallengeData();

        Challenge challenge2 = testDataSetup.챌린지를_생성한다(
                "제목입니다.2",
                "내용입니다.2",
                ECONOMY.getDescription(),
                OUTDOOR.getDescription(),
                OVER_ONE_HOUR.getDescription(),
                user
        );
        testDataSetup.챌린지에_참가한다(challenge2, user);
        testDataSetup.챌린지에_해시태그를_단다(challenge2, List.of("tag1", "tag2", "tag3"));

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
                testDataSetup.챌린지에_해시태그를_단다(challenge, List.of("tag2", "tag4"));
                challenge6 = challenge;
            }
        }

        for (int i = 1; i <= 8; i++) {
            User otherUser = testDataSetup.saveUser(USERNAME + i, i + "@test.com", PASSWORD);
            if (i == 1) {
                testDataSetup.챌린지에_참가한다(challenge1, otherUser);
            }
            if (2 <= i && i <= 5) {
                testDataSetup.챌린지에_참가한다(challenge2, otherUser);
            }
            if (i == 6) {
                testDataSetup.챌린지에_참가한다(challenge6, otherUser);
            }
        }
    }

    @Test
    @DisplayName("챌린지 생성 테스트")
//    @WithAuthUser
    void createChallengeTest() throws Exception {
        testDataSetup.saveUserBadgeEvaluation(user);

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
                .andExpect(jsonPath("$.challengeOwnerUser.userId").value(user.getId()));
    }

    @Test
    @DisplayName("특정 챌린지 조회 테스트")
    void findChallengeByIdTest() throws Exception {
        initData();

        Long challenge1Id = challenge1.getId();

        mockMvc.perform(get("/challenge/{challengeId}", challenge1Id)
                        .with(requestPostProcessor)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.responseChallenge.title").value(challenge1.getTitle()))
                .andExpect(jsonPath("$.responseChallenge.content").value(challenge1.getContent()))
                .andExpect(jsonPath("$.responseChallenge.challengeCategory").value(
                        challenge1.getChallengeCategory().getDescription()))
                .andExpect(jsonPath("$.responseChallenge.challengeLocation").value(
                        challenge1.getChallengeLocation().getDescription()))
                .andExpect(jsonPath("$.responseChallenge.challengeDuration").value(
                        challenge1.getChallengeDuration().getDescription()))
                .andExpect(jsonPath("$.responseChallenge.created_at").value(challenge1.getFormattedCreatedAt()))
                .andExpect(
                        jsonPath("$.responseChallenge.challengeImgUrls[*]").value(challenge1.getImgUrls()))
                .andExpect(
                        jsonPath("$.responseChallenge.challengeHashtags[*]").value(challenge1.getHashtags()))
                .andExpect(jsonPath("$.responseChallenge.howManyUsersAreInThisChallenge").value(2))
                .andExpect(
                        jsonPath("$.responseChallenge.challengeOwnerUser.userName").value(user.getUserName()))
                .andExpect(jsonPath("$.responseChallenge.challengeOwnerUser.email").value(user.getEmail()))
                .andExpect(jsonPath("$.responseChallenge.challengeOwnerUser.userId").value(user.getId()))
                .andExpect(jsonPath("$.responseUserChallenges[*].challengeStatus",
                        hasItem(ChallengeStatus.TRYING.getDescription())))
                .andExpect(jsonPath("$.responseUserChallenges[*].participatedUser.userName",
                        contains(user.getUserName(), "홍길동1")))
                .andExpect(jsonPath("$.responseUserChallenges[*].participatedUser.email",
                        contains(user.getEmail(), "1@test.com")));
    }

    @Test
    @DisplayName("모든 챌린지 조회 테스트")
    void searchAllChallengesTest() throws Exception {
        initData();

        mockMvc.perform(get("/challenge")
                        .param("size", "20")
                        .param("page", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].title", contains(
                        "제목입니다.2", "제목입니다.1", "제목입니다.6", "제목입니다.3", "제목입니다.4",
                        "제목입니다.5", "제목입니다.7", "제목입니다.8", "제목입니다.9", "제목입니다.10")))
                .andExpect(jsonPath("$.content[*].content", contains(
                        "내용입니다.2", "내용입니다.1", "내용입니다.6", "내용입니다.3", "내용입니다.4",
                        "내용입니다.5", "내용입니다.7", "내용입니다.8", "내용입니다.9", "내용입니다.10")))
                .andExpect(jsonPath("$.content[*].challengeCategory",
                        hasItems(ECONOMY.getDescription(), STUDY.getDescription(),
                                WORKOUT.getDescription())))
                .andExpect(jsonPath("$.content[*].challengeLocation",
                        hasItems(OUTDOOR.getDescription(),
                                INDOOR.getDescription())))
                .andExpect(jsonPath("$.content[*].challengeDuration",
                        hasItems(OVER_ONE_HOUR.getDescription(),
                                WITHIN_TEN_MINUTES.getDescription())))
                .andExpect(jsonPath("$.content[*].challengeImgUrls",
                        hasItems(hasItem(startsWith("/images/")))))
                .andExpect(jsonPath("$.content[*].challengeHashtags",
                        hasItems(List.of("tag1", "tag2"))))
                .andExpect(jsonPath("$.content[*].howManyUsersAreInThisChallenge",
                        contains(5, 2, 2, 1, 1, 1, 1, 1, 1, 1)))
                .andExpect(jsonPath("$.content[*].challengeOwnerUser.userName",
                        hasItem(user.getUserName())))
                .andExpect(jsonPath("$.content[*].challengeOwnerUser.email",
                        hasItem(user.getEmail())))
                .andExpect(jsonPath("$.content[*].challengeOwnerUser.userId",
                        hasItem(user.getId().intValue())));
    }

    @Test
    @DisplayName("해시태그로 검색한 챌린지 조회 테스트")
    void searchChallengesByHashtagTest() throws Exception {
        initData();

        mockMvc.perform(get("/challenge/hashtag")
                        .param("size", "10")
                        .param("page", "0")
                        .param("content", "tag2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].title", contains(
                        "제목입니다.1", "제목입니다.2", "제목입니다.6")))
                .andExpect(jsonPath("$.content[*].content", contains(
                        "내용입니다.1", "내용입니다.2", "내용입니다.6")))
                .andExpect(jsonPath("$.content[*].challengeCategory",
                        hasItems(ECONOMY.getDescription(), STUDY.getDescription(),
                                WORKOUT.getDescription())))
                .andExpect(jsonPath("$.content[*].challengeLocation",
                        hasItems(OUTDOOR.getDescription(),
                                INDOOR.getDescription())))
                .andExpect(jsonPath("$.content[*].challengeDuration",
                        hasItems(OVER_ONE_HOUR.getDescription(),
                                WITHIN_TEN_MINUTES.getDescription())))
                .andExpect(jsonPath("$.content[*].challengeImgUrls",
                        hasItems(hasItem(startsWith("/images/")))))
                .andExpect(jsonPath("$.content[*].challengeOwnerUser.userName",
                        hasItem(user.getUserName())))
                .andExpect(jsonPath("$.content[*].challengeOwnerUser.email",
                        hasItem(user.getEmail())))
                .andExpect(jsonPath("$.content[*].challengeOwnerUser.userId",
                        hasItem(user.getId().intValue())));
    }

    static Stream<Arguments> generateConditionData() {
        return Stream.of(
                Arguments.of(ChallengeSearchCondition.builder()
                                .title("1").category(null).build(),
                        "popular",
                        List.of(
                                contains("제목입니다.1", "제목입니다.10"),
                                contains("내용입니다.1", "내용입니다.10"),
                                contains(STUDY.getDescription(),
                                        WORKOUT.getDescription()),
                                hasItem(INDOOR.getDescription()),
                                hasItem(WITHIN_TEN_MINUTES.getDescription()),
                                hasItem(hasItem(startsWith("/images/"))),
                                contains(2, 1)
                        )),
                Arguments.of(ChallengeSearchCondition.builder()
                                .title(null).category(WORKOUT.getDescription()).build(),
                        "popular",
                        List.of(
                                contains("제목입니다.6", "제목입니다.3", "제목입니다.4", "제목입니다.5", "제목입니다.7", "제목입니다.8",
                                        "제목입니다.9", "제목입니다.10"),
                                contains("내용입니다.6", "내용입니다.3", "내용입니다.4", "내용입니다.5", "내용입니다.7", "내용입니다.8",
                                        "내용입니다.9", "내용입니다.10"),
                                hasItem(WORKOUT.getDescription()),
                                hasItem(INDOOR.getDescription()),
                                hasItem(WITHIN_TEN_MINUTES.getDescription()),
                                hasItem(hasItem(startsWith("/images/"))),
                                contains(2, 1, 1, 1, 1, 1, 1, 1)
                        )),
                Arguments.of(ChallengeSearchCondition.builder()
                                .title(null).category(WORKOUT.getDescription()).build(),
                        "time",
                        List.of(
                                contains("제목입니다.10", "제목입니다.9", "제목입니다.8", "제목입니다.7", "제목입니다.6", "제목입니다.5",
                                        "제목입니다.4", "제목입니다.3"),
                                contains("내용입니다.10", "내용입니다.9", "내용입니다.8", "내용입니다.7", "내용입니다.6", "내용입니다.5",
                                        "내용입니다.4", "내용입니다.3"),
                                hasItem(WORKOUT.getDescription()),
                                hasItem(INDOOR.getDescription()),
                                hasItem(WITHIN_TEN_MINUTES.getDescription()),
                                hasItem(hasItem(startsWith("/images/"))),
                                contains(1, 1, 1, 1, 2, 1, 1, 1)
                        )),
                Arguments.of(ChallengeSearchCondition.builder()
                                .title("1").category(STUDY.getDescription()).build(),
                        "popular",
                        List.of(
                                contains("제목입니다.1"),
                                contains("내용입니다.1"),
                                contains(STUDY.getDescription()),
                                contains(INDOOR.getDescription()),
                                contains(WITHIN_TEN_MINUTES.getDescription()),
                                hasItem(hasItem(startsWith("/images/"))),
                                contains(2))
                ));
    }

    @ParameterizedTest
    @MethodSource("generateConditionData")
    @DisplayName("챌린지들을 검색 조건으로 찾는 테스트")
    void searchChallengesByConditionTest(ChallengeSearchCondition condition, String sortProperties,
                                         List<Matcher<Iterable<? extends String>>> expects) throws Exception {
        initData();

        mockMvc.perform(get("/challenge/condition")
                        .param("title", condition.getTitle())
                        .param("category", condition.getCategory())
                        .param("size", "20")
                        .param("page", "0")
                        .param("sort", sortProperties)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].title", expects.get(0)))
                .andExpect(jsonPath("$.content[*].content", expects.get(1)))
                .andExpect(jsonPath("$.content[*].challengeCategory", expects.get(2)))
                .andExpect(jsonPath("$.content[*].challengeLocation", expects.get(3)))
                .andExpect(jsonPath("$.content[*].challengeDuration", expects.get(4)))
                .andExpect(jsonPath("$.content[*].challengeImgUrls", expects.get(5)))
                .andExpect(jsonPath("$.content[*].howManyUsersAreInThisChallenge", expects.get(6)))
                .andExpect(jsonPath("$.content[*].challengeOwnerUser.userName", hasItem(
                        user.getUserName())))
                .andExpect(jsonPath("$.content[*].challengeOwnerUser.email", hasItem(
                        user.getEmail())))
                .andExpect(jsonPath("$.content[*].challengeOwnerUser.userId", hasItem(
                        user.getId().intValue())));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "10, 2"
    })
    @DisplayName("모든 챌린지 조회 페이징 테스트")
    void searchAllChallengesPagingTest(int totalElements, int numOfPage) throws Exception {
        initData();

        mockMvc.perform(get("/challenge")
                        .param("size", String.valueOf(numOfPage))
                        .param("page", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(totalElements))
                .andExpect(jsonPath("$.totalPages").value(totalElements / numOfPage));
    }

    @Test
    @DisplayName("챌린지들을 질문으로 찾는 테스트")
    void searchChallengesByQuestionTest() throws Exception {
        initData();

        mockMvc.perform(get("/challenge/question")
                        .param("challengeLocationIndex", "1")
                        .param("challengeDurationIndex", "0")
                        .param("challengeCategoryIndex", "2")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*].id").isNotEmpty())
                .andExpect(jsonPath("$.[*].title", contains(
                        "제목입니다.3", "제목입니다.4", "제목입니다.5", "제목입니다.6")))
                .andExpect(jsonPath("$.[*].content", contains(
                        "내용입니다.3", "내용입니다.4", "내용입니다.5", "내용입니다.6")))
                .andExpect(jsonPath("$.[*].challengeImgUrls").isNotEmpty());
    }

    @Test
    @DisplayName("챌린지들을 질문으로 찾을 수 없는 경우 랜덤 챌린지를 반환하는 테스트")
    void searchChallengesByQuestionWithNotMatchTest() throws Exception {
        initData();

        mockMvc.perform(get("/challenge/question")
                        .param("challengeLocationIndex", "1")
                        .param("challengeDurationIndex", "3")
                        .param("challengeCategoryIndex", "4")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*].id").isNotEmpty())
                .andExpect(jsonPath("$.[*].title", hasSize(1)))
                .andExpect(jsonPath("$.[*].content", hasSize(1)))
                .andExpect(jsonPath("$.[*].challengeImgUrls").isNotEmpty());
    }

    @Test
    @DisplayName("챌린지들을 해시태그들로 찾는 테스트")
    void searchChallengesByHashtagsTest() throws Exception {
        initData();

        mockMvc.perform(get("/challenge/hashtags")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*].hashtagId").isNotEmpty())
                .andExpect(jsonPath("$.[*].hashtagContent", hasItems("tag2", "tag1", "tag3")))
                .andExpect(jsonPath("$.[*].hashtagTagCount", hasItems(3, 2, 1)))
                .andExpect(jsonPath("$.[*].recommendedChallenges").isNotEmpty());
    }

    @Test
    @DisplayName("챌린지를 랜덤으로 찾는 테스트")
    void searchChallengeByRandomTest() throws Exception {
        initData();

        mockMvc.perform(get("/challenge/random")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title", startsWith("제목입니다.")))
                .andExpect(jsonPath("$.content", startsWith("내용입니다.")))
                .andExpect(jsonPath("$.challengeImgUrls").isNotEmpty());
    }

    @Test
    @DisplayName("챌린지 수정 테스트")
    void updateChallenge() throws Exception {
        initChallengeData();

        RequestUpdateChallenge requestUpdateChallenge = RequestUpdateChallenge.builder()
                .title("수정된 제목")
                .content("수정된 내용")
                .challengeCategory(WORKOUT.getDescription())
                .build();
        List<MultipartFile> updateChallengeImgFiles = updateChallengeImgFiles();

        String json = objectMapper.writeValueAsString(requestUpdateChallenge);
        MockMultipartFile mockRequestUpdateChallenge = new MockMultipartFile("requestUpdateChallenge",
                "requestUpdateChallenge",
                "application/json", json.getBytes(UTF_8));

        HashtagDto hashtagDto = HashtagDto.builder()
                .content(List.of("editTag1", "editTag2"))
                .build();
        String hashtagDtoJson = objectMapper.writeValueAsString(hashtagDto);
        MockMultipartFile mockHashtagDto = new MockMultipartFile("hashtagDto",
                "hashtagDto",
                "application/json", hashtagDtoJson.getBytes(UTF_8));

        Long challenge1Id = challenge1.getId();

        mockMvc.perform(multipart("/challenge/{challengeId}", challenge1Id)
                        .file(mockRequestUpdateChallenge)
                        .part(new MockPart("updateChallengeImgFiles", "updateChallengeImgFiles",
                                updateChallengeImgFiles.get(0).getBytes()))
                        .part(new MockPart("updateChallengeImgFiles", "updateChallengeImgFiles",
                                updateChallengeImgFiles.get(1).getBytes()))
                        .file(mockHashtagDto)
                        .with(requestPostProcessor)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(requestUpdateChallenge.getTitle()))
                .andExpect(jsonPath("$.content").value(requestUpdateChallenge.getContent()))
                .andExpect(jsonPath("$.challengeCategory").value(requestUpdateChallenge.getChallengeCategory()))
                .andExpect(
                        jsonPath("$.challengeLocation").value(challenge1.getChallengeLocation().getDescription()))
                .andExpect(
                        jsonPath("$.challengeDuration").value(challenge1.getChallengeDuration().getDescription()))
                .andExpect(jsonPath("$.created_at").value(challenge1.getFormattedCreatedAt()))
                .andExpect(jsonPath("$.updated_at").isNotEmpty())
                .andExpect(jsonPath("$.challengeImgUrls[*]", hasItem(startsWith("/images/"))))
                .andExpect(jsonPath("$.challengeHashtags[*]", contains("editTag1", "editTag2")))
                .andExpect(jsonPath("$.challengeImgUrls", hasSize(2)));
    }

    @Test
    @DisplayName("챌린지 삭제 테스트")
    void deleteChallenge() throws Exception {
        initChallengeData();
        Long challenge1Id = challenge1.getId();

        mockMvc.perform(delete("/challenge/{challengeId}", challenge1Id)
                        .with(requestPostProcessor)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}