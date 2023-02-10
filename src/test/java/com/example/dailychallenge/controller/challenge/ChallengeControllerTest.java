package com.example.dailychallenge.controller.challenge;

import static com.example.dailychallenge.util.fixture.ChallengeImgFixture.createChallengeImgFiles;
import static com.example.dailychallenge.util.fixture.ChallengeImgFixture.updateChallengeImgFiles;
import static com.example.dailychallenge.util.fixture.UserFixture.createUser;
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
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.ChallengeCategory;
import com.example.dailychallenge.entity.challenge.ChallengeDuration;
import com.example.dailychallenge.entity.challenge.ChallengeLocation;
import com.example.dailychallenge.entity.challenge.ChallengeStatus;
import com.example.dailychallenge.entity.comment.Comment;
import com.example.dailychallenge.entity.hashtag.ChallengeHashtag;
import com.example.dailychallenge.entity.hashtag.Hashtag;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.repository.ChallengeHashtagRepository;
import com.example.dailychallenge.repository.CommentRepository;
import com.example.dailychallenge.repository.HashtagRepository;
import com.example.dailychallenge.repository.UserRepository;
import com.example.dailychallenge.service.challenge.ChallengeService;
import com.example.dailychallenge.service.challenge.UserChallengeService;
import com.example.dailychallenge.service.users.UserService;
import com.example.dailychallenge.util.ControllerTest;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.web.multipart.MultipartFile;

class ChallengeControllerTest extends ControllerTest {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserService userService;
    @Autowired
    private ChallengeService challengeService;
    @Autowired
    private UserRepository userRepository;
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
            User user = User.builder()
                    .userName("홍길동" + i)
                    .email(i + "@test.com")
                    .password("1234")
                    .build();
            userRepository.save(user);
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
    }

    @Test
    @DisplayName("챌린지 생성 테스트")
//    @WithAuthUser
    void createChallengeTest() throws Exception {
        RequestCreateChallenge requestCreatChallenge = RequestCreateChallenge.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .challengeCategory("공부")
                .challengeLocation("실내")
                .challengeDuration("10분 이내")
                .build();
        List<MultipartFile> challengeImgFiles = createChallengeImgFiles();

        String json = objectMapper.writeValueAsString(requestCreatChallenge);
        MockMultipartFile requestCreateChallenge = new MockMultipartFile("requestCreateChallenge",
                "requestCreateChallenge",
                "application/json", json.getBytes(UTF_8));

        MockPart tag1 = new MockPart("\"hashtagDto\"", "tag1".getBytes(UTF_8));
        MockPart tag2 = new MockPart("\"hashtagDto\"", "tag2".getBytes(UTF_8));
        // TODO: 2023-02-07 현재 hashtag 값이 안 넘어와서  hashtag 테스트 코드 수정하기

        mockMvc.perform(multipart("/challenge/new")
                        .file(requestCreateChallenge)
                        .part(new MockPart("challengeImgFiles", "challengeImgFile", challengeImgFiles.get(0).getBytes()))
                        .part(new MockPart("challengeImgFiles", "challengeImgFile", challengeImgFiles.get(1).getBytes()))
                        .part(new MockPart("challengeImgFiles", "challengeImgFile", challengeImgFiles.get(2).getBytes()))
                        .part(tag1)
                        .part(tag2)
                        .with(requestPostProcessor) // 토큰 인증 처리, 입력한 정보로 인증된 사용자 생성
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(requestCreatChallenge.getTitle()))
                .andExpect(jsonPath("$.content").value(requestCreatChallenge.getContent()))
                .andExpect(jsonPath("$.challengeCategory").value(requestCreatChallenge.getChallengeCategory()))
                .andExpect(jsonPath("$.challengeLocation").value(requestCreatChallenge.getChallengeLocation()))
                .andExpect(jsonPath("$.challengeDuration").value(requestCreatChallenge.getChallengeDuration()))
                .andExpect(jsonPath("$.challengeStatus").value(ChallengeStatus.TRYING.getDescription()))
                .andExpect(jsonPath("$.challengeImgUrls[*]", hasItem(startsWith("/images/"))))
                .andExpect(jsonPath("$.challengeOwnerUser.userName").value(savedUser.getUserName()))
                .andExpect(jsonPath("$.challengeOwnerUser.email").value(savedUser.getEmail()))
                .andExpect(jsonPath("$.challengeOwnerUser.userId").value(savedUser.getId()));
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
                .andExpect(jsonPath("$.responseChallenge.howManyUsersAreInThisChallenge").value(2))
                .andExpect(
                        jsonPath("$.responseChallenge.challengeOwnerUser.userName").value(savedUser.getUserName()))
                .andExpect(jsonPath("$.responseChallenge.challengeOwnerUser.email").value(savedUser.getEmail()))
                .andExpect(jsonPath("$.responseChallenge.challengeOwnerUser.userId").value(savedUser.getId()))
                .andExpect(jsonPath("$.responseUserChallenges[*].challengeStatus",
                        hasItem(ChallengeStatus.TRYING.getDescription())))
                .andExpect(jsonPath("$.responseUserChallenges[*].participatedUser.userName",
                        contains(savedUser.getUserName(), "홍길동1")))
                .andExpect(jsonPath("$.responseUserChallenges[*].participatedUser.email",
                        contains(savedUser.getEmail(), "1@test.com")));
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
                        hasItems(ChallengeCategory.ECONOMY.getDescription(), ChallengeCategory.STUDY.getDescription(),
                                ChallengeCategory.WORKOUT.getDescription())))
                .andExpect(jsonPath("$.content[*].challengeLocation",
                        hasItems(ChallengeLocation.OUTDOOR.getDescription(),
                                ChallengeLocation.INDOOR.getDescription())))
                .andExpect(jsonPath("$.content[*].challengeDuration",
                        hasItems(ChallengeDuration.OVER_ONE_HOUR.getDescription(),
                                ChallengeDuration.WITHIN_TEN_MINUTES.getDescription())))
                .andExpect(jsonPath("$.content[*].challengeImgUrls",
                        hasItems(hasItem(startsWith("/images/")))))
                .andExpect(jsonPath("$.content[*].howManyUsersAreInThisChallenge",
                        contains(5, 2, 2, 1, 1, 1, 1, 1, 1, 1)))
                .andExpect(jsonPath("$.content[*].challengeOwnerUser.userName",
                        hasItem(savedUser.getUserName())))
                .andExpect(jsonPath("$.content[*].challengeOwnerUser.email",
                        hasItem(savedUser.getEmail())))
                .andExpect(jsonPath("$.content[*].challengeOwnerUser.userId",
                        hasItem(savedUser.getId().intValue())));
    }

    static Stream<Arguments> generateConditionData() {
        return Stream.of(
                Arguments.of(ChallengeSearchCondition.builder()
                                .title("1").category(null).build(),
                        "popular",
                        List.of(
                                contains("제목입니다.1", "제목입니다.10"),
                                contains("내용입니다.1", "내용입니다.10"),
                                contains(ChallengeCategory.STUDY.getDescription(),
                                        ChallengeCategory.WORKOUT.getDescription()),
                                hasItem(ChallengeLocation.INDOOR.getDescription()),
                                hasItem(ChallengeDuration.WITHIN_TEN_MINUTES.getDescription()),
                                hasItem(hasItem(startsWith("/images/"))),
                                contains(2, 1)
                        )),
                Arguments.of(ChallengeSearchCondition.builder()
                                .title(null).category(ChallengeCategory.WORKOUT.getDescription()).build(),
                        "popular",
                        List.of(
                                contains("제목입니다.6", "제목입니다.3", "제목입니다.4", "제목입니다.5", "제목입니다.7", "제목입니다.8",
                                        "제목입니다.9", "제목입니다.10"),
                                contains("내용입니다.6", "내용입니다.3", "내용입니다.4", "내용입니다.5", "내용입니다.7", "내용입니다.8",
                                        "내용입니다.9", "내용입니다.10"),
                                hasItem(ChallengeCategory.WORKOUT.getDescription()),
                                hasItem(ChallengeLocation.INDOOR.getDescription()),
                                hasItem(ChallengeDuration.WITHIN_TEN_MINUTES.getDescription()),
                                hasItem(hasItem(startsWith("/images/"))),
                                contains(2, 1, 1, 1, 1, 1, 1, 1)
                        )),
                Arguments.of(ChallengeSearchCondition.builder()
                                .title(null).category(ChallengeCategory.WORKOUT.getDescription()).build(),
                        "time",
                        List.of(
                                contains("제목입니다.10", "제목입니다.9", "제목입니다.8", "제목입니다.7", "제목입니다.6", "제목입니다.5",
                                        "제목입니다.4", "제목입니다.3"),
                                contains("내용입니다.10", "내용입니다.9", "내용입니다.8", "내용입니다.7", "내용입니다.6", "내용입니다.5",
                                        "내용입니다.4", "내용입니다.3"),
                                hasItem(ChallengeCategory.WORKOUT.getDescription()),
                                hasItem(ChallengeLocation.INDOOR.getDescription()),
                                hasItem(ChallengeDuration.WITHIN_TEN_MINUTES.getDescription()),
                                hasItem(hasItem(startsWith("/images/"))),
                                contains(1, 1, 1, 1, 2, 1, 1, 1)
                        )),
                Arguments.of(ChallengeSearchCondition.builder()
                                .title("1").category(ChallengeCategory.STUDY.getDescription()).build(),
                        "popular",
                        List.of(
                                contains("제목입니다.1"),
                                contains("내용입니다.1"),
                                contains(ChallengeCategory.STUDY.getDescription()),
                                contains(ChallengeLocation.INDOOR.getDescription()),
                                contains(ChallengeDuration.WITHIN_TEN_MINUTES.getDescription()),
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
                        savedUser.getUserName())))
                .andExpect(jsonPath("$.content[*].challengeOwnerUser.email", hasItem(
                        savedUser.getEmail())))
                .andExpect(jsonPath("$.content[*].challengeOwnerUser.userId", hasItem(
                        savedUser.getId().intValue())));
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

    @Test
    @DisplayName("챌린지 수정 테스트")
    void updateChallenge() throws Exception {
        RequestUpdateChallenge requestUpdateChallenge = RequestUpdateChallenge.builder()
                .title("수정된 제목")
                .content("수정된 내용")
                .challengeCategory(ChallengeCategory.WORKOUT.getDescription())
                .build();
        List<MultipartFile> updateChallengeImgFiles = updateChallengeImgFiles();

        String json = objectMapper.writeValueAsString(requestUpdateChallenge);
        MockMultipartFile mockRequestUpdateChallenge = new MockMultipartFile("requestUpdateChallenge",
                "requestUpdateChallenge",
                "application/json", json.getBytes(UTF_8));

        Long challenge1Id = challenge1.getId();
        mockMvc.perform(multipart("/challenge/{challengeId}", challenge1Id)
                        .file(mockRequestUpdateChallenge)
                        .part(new MockPart("updateChallengeImgFiles", "updateChallengeImgFiles",
                                updateChallengeImgFiles.get(0).getBytes()))
                        .part(new MockPart("updateChallengeImgFiles", "updateChallengeImgFiles",
                                updateChallengeImgFiles.get(1).getBytes()))
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