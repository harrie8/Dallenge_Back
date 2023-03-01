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
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.dailychallenge.dto.ChallengeDto;
import com.example.dailychallenge.dto.ChallengeSearchCondition;
import com.example.dailychallenge.dto.HashtagDto;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.ChallengeStatus;
import com.example.dailychallenge.entity.comment.Comment;
import com.example.dailychallenge.entity.hashtag.Hashtag;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.repository.CommentRepository;
import com.example.dailychallenge.service.challenge.ChallengeService;
import com.example.dailychallenge.service.challenge.UserChallengeService;
import com.example.dailychallenge.service.hashtag.ChallengeHashtagService;
import com.example.dailychallenge.service.hashtag.HashtagService;
import com.example.dailychallenge.util.ControllerTest;
import com.example.dailychallenge.util.fixture.TestDataSetup;
import com.example.dailychallenge.vo.challenge.RequestCreateChallenge;
import com.example.dailychallenge.vo.challenge.RequestUpdateChallenge;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
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
    private ChallengeService challengeService;
    @Autowired
    private UserChallengeService userChallengeService;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private HashtagService hashtagService;
    @Autowired
    private ChallengeHashtagService challengeHashtagService;

    @Autowired
    private TestDataSetup testDataSetup;

    private User user;
    private RequestPostProcessor requestPostProcessor;

    @BeforeEach
    void beforeEach() {
        user = testDataSetup.saveUser(USERNAME, EMAIL, PASSWORD);
        requestPostProcessor = user(new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(),
                true, true, true, true,
                new ArrayList<>()
        ));
    }

    private Challenge 챌린지를_생성한다(String title, String content, String challengeCategoryDescription,
                                String challengeLocationDescription, String challengeDurationDescription,
                                User user) {
        ChallengeDto challengeDto = ChallengeDto.builder()
                .title(title)
                .content(content)
                .challengeCategory(challengeCategoryDescription)
                .challengeLocation(challengeLocationDescription)
                .challengeDuration(challengeDurationDescription)
                .build();

        return challengeService.saveChallenge(challengeDto, createChallengeImgFiles(), user);
    }

    private void 챌린지에_참가한다(Challenge challenge, User user) {
        userChallengeService.saveUserChallenge(challenge, user);
    }

    private void 챌린지예_댓글을_단다(Challenge challenge) {
        Comment comment = Comment.builder()
                .content("content")
                .build();
        comment.saveCommentChallenge(challenge);
        commentRepository.save(comment);
    }

    private void 챌린지에_해시태그를_단다(Challenge challenge) {
        List<String> hashtagDto = List.of("tag1", "tag2");
        List<Hashtag> hashtags = hashtagService.saveHashtag(hashtagDto);
        challengeHashtagService.saveChallengeHashtag(challenge, hashtags);
    }

    @Test
    @DisplayName("챌린지 생성 테스트")
//    @WithAuthUser
    void createChallengeTest() throws Exception {
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

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    @DisplayName("조회 테스트는 초기값이 필요하다.")
    class find {
        private Challenge challenge1;
        @BeforeEach
        void beforeEach() {
            challenge1 = 챌린지를_생성한다(
                    "제목입니다.1",
                    "내용입니다.1",
                    STUDY.getDescription(),
                    INDOOR.getDescription(),
                    WITHIN_TEN_MINUTES.getDescription(),
                    user);
            챌린지에_참가한다(challenge1, user);
            챌린지예_댓글을_단다(challenge1);
            챌린지에_해시태그를_단다(challenge1);

            Challenge challenge2 = 챌린지를_생성한다(
                    "제목입니다.2",
                    "내용입니다.2",
                    ECONOMY.getDescription(),
                    OUTDOOR.getDescription(),
                    OVER_ONE_HOUR.getDescription(),
                    user
            );
            챌린지에_참가한다(challenge2, user);

            Challenge challenge6 = null;

            for (int i = 3; i <= 10; i++) {
                Challenge challenge = 챌린지를_생성한다(
                        "제목입니다." + i,
                        "내용입니다." + i,
                        WORKOUT.getDescription(),
                        INDOOR.getDescription(),
                        WITHIN_TEN_MINUTES.getDescription(),
                        user
                );
                챌린지에_참가한다(challenge, user);

                if (i == 6) {
                    challenge6 = challenge;
                }
            }

            for (int i = 1; i <= 8; i++) {
                User otherUser = testDataSetup.saveUser(USERNAME + i, i + "@test.com", PASSWORD);
                if (i == 1) {
                    챌린지에_참가한다(challenge1, otherUser);
                }
                if (2 <= i && i <= 5) {
                    챌린지에_참가한다(challenge2, otherUser);
                }
                if (i == 6) {
                    챌린지에_참가한다(challenge6, otherUser);
                }
            }
        }

        @Test
        @DisplayName("특정 챌린지 조회 테스트")
        void findChallengeByIdTest() throws Exception {
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
            mockMvc.perform(get("/challenge")
                            .with(requestPostProcessor)
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

        Stream<Arguments> generateConditionData() {
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
            mockMvc.perform(get("/challenge/condition")
                            .with(requestPostProcessor)
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
            mockMvc.perform(get("/challenge")
                            .with(requestPostProcessor)
                            .param("size", String.valueOf(numOfPage))
                            .param("page", "0")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalElements").value(totalElements))
                    .andExpect(jsonPath("$.totalPages").value(totalElements / numOfPage));
        }
    }

    @Nested
    @DisplayName("챌린지가 존재해야만 하는 테스트 모음")
    class UpdateAndDelete {
        private Challenge challenge1;
        @BeforeEach
        void beforeEach() {
            challenge1 = 챌린지를_생성한다(
                    "제목입니다.1",
                    "내용입니다.1",
                    STUDY.getDescription(),
                    INDOOR.getDescription(),
                    WITHIN_TEN_MINUTES.getDescription(),
                    user);
            챌린지에_참가한다(challenge1, user);
            챌린지예_댓글을_단다(challenge1);
            챌린지에_해시태그를_단다(challenge1);
        }

        @Test
        @DisplayName("챌린지 수정 테스트")
        void updateChallenge() throws Exception {
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
            Long challenge1Id = challenge1.getId();

            mockMvc.perform(delete("/challenge/{challengeId}", challenge1Id)
                            .with(requestPostProcessor)
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNoContent());
        }
    }
}