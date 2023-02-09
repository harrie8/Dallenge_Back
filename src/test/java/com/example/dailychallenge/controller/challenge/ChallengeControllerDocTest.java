package com.example.dailychallenge.controller.challenge;

import static com.example.dailychallenge.util.fixture.TokenFixture.EMAIL;
import static com.example.dailychallenge.util.fixture.TokenFixture.PASSWORD;
import static com.example.dailychallenge.util.fixture.TokenFixture.ROLE;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.removeHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.dailychallenge.dto.ChallengeDto;
import com.example.dailychallenge.dto.UserDto;
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
import com.example.dailychallenge.repository.ChallengeImgRepository;
import com.example.dailychallenge.repository.ChallengeRepository;
import com.example.dailychallenge.repository.CommentRepository;
import com.example.dailychallenge.repository.HashtagRepository;
import com.example.dailychallenge.repository.UserChallengeRepository;
import com.example.dailychallenge.repository.UserRepository;
import com.example.dailychallenge.service.challenge.ChallengeService;
import com.example.dailychallenge.service.challenge.UserChallengeService;
import com.example.dailychallenge.service.users.UserService;
import com.example.dailychallenge.vo.challenge.RequestCreateChallenge;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.dailychallenge.com", uriPort = 443)
@ExtendWith(RestDocumentationExtension.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class ChallengeControllerDocTest {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserService userService;
    @Autowired
    private ChallengeService challengeService;
    @Autowired
    private ChallengeRepository challengeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserChallengeRepository userChallengeRepository;
    @Autowired
    private UserChallengeService userChallengeService;
    @Autowired
    private ChallengeImgRepository challengeImgRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private HashtagRepository hashtagRepository;
    @Autowired
    private ChallengeHashtagRepository challengeHashtagRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @Value("${userImgLocation}")
    private String challengeImgLocation;
    private User savedUser;
    private Challenge challenge1;
    private UserRequestPostProcessor userRequestPostProcessor;

    @BeforeEach
    void beforeEach() throws Exception {
        initData();
        userRequestPostProcessor = user(EMAIL).password(PASSWORD).roles(ROLE);
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

    public UserDto createUser() {
        UserDto userDto = new UserDto();
        userDto.setEmail("test1234@test.com");
        userDto.setUserName("홍길동");
        userDto.setInfo("testInfo");
        userDto.setPassword("1234");
        return userDto;
    }
    private List<MultipartFile> createChallengeImgFiles() {
        List<MultipartFile> result = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            String path = challengeImgLocation +"/";
            String imageName = "challengeImage" + i + ".jpg";
            result.add(new MockMultipartFile(path, imageName, "image/jpg", new byte[]{1, 2, 3, 4}));
        }
        return result;
    }

    @Test
    @DisplayName("챌린지 생성")
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
                "application/json", json.getBytes(
                StandardCharsets.UTF_8));

        MockPart tag1 = new MockPart("\"hashtagDto\"", "tag1".getBytes(UTF_8));
        MockPart tag2 = new MockPart("\"hashtagDto\"", "tag2".getBytes(UTF_8));

        String challengeCategoryDescriptions = String.join(", ", ChallengeCategory.getDescriptions());
        String challengeLocationDescriptions = String.join(", ", ChallengeLocation.getDescriptions());
        String challengeDurationDescriptions = String.join(", ", ChallengeDuration.getDescriptions());
        mockMvc.perform(multipart("/challenge/new")
                        .file(requestCreateChallenge)
                        .part(new MockPart("challengeImgFiles", "challengeImgFile", challengeImgFiles.get(0).getBytes()))
                        .part(new MockPart("challengeImgFiles", "challengeImgFile", challengeImgFiles.get(1).getBytes()))
                        .part(new MockPart("challengeImgFiles", "challengeImgFile", challengeImgFiles.get(2).getBytes()))
                        .part(tag1)
                        .part(tag2)
                        .with(userRequestPostProcessor)
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
                .andExpect(jsonPath("$.challengeOwnerUser.userId").value(savedUser.getId()))
                .andDo(print())
                .andDo(document("challenge-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(
                                removeHeaders("Vary", "X-Content-Type-Options", "X-XSS-Protection", "Pragma", "Expires",
                                        "Cache-Control", "Strict-Transport-Security", "X-Frame-Options"),
                                prettyPrint()),
                        requestParts(
                                partWithName("requestCreateChallenge").description("챌린지 정보 데이터(JSON)")
                                        .attributes(key("type").value("JSON")),
                                partWithName("challengeImgFiles").description("챌린지 이미지 파일들(FILE)").optional()
                                        .attributes(key("type").value(".jpg")),
                                partWithName("\"hashtagDto\"").description("해시태그 데이터(LIST)").optional()
                                        .attributes(key("type").value("LIST"))
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
                        )
                ));
    }

    @Test
    @DisplayName("특정 챌린지 조회 테스트")
    void findChallengeByIdTest() throws Exception {
        Long challenge1Id = challenge1.getId();

        mockMvc.perform(get("/challenge/{challengeId}", challenge1Id)
                        .with(userRequestPostProcessor)
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
                .andExpect(jsonPath("$.responseChallenge.challengeOwnerUser.userName").value(savedUser.getUserName()))
                .andExpect(jsonPath("$.responseChallenge.challengeOwnerUser.email").value(savedUser.getEmail()))
                .andExpect(jsonPath("$.responseChallenge.challengeOwnerUser.userId").value(savedUser.getId()))
                .andExpect(jsonPath("$.responseUserChallenges[*].challengeStatus",
                        hasItem(ChallengeStatus.TRYING.getDescription())))
                .andExpect(jsonPath("$.responseUserChallenges[*].participatedUser.userName",
                        contains(savedUser.getUserName(), "홍길동1")))
                .andExpect(jsonPath("$.responseUserChallenges[*].participatedUser.email",
                        contains(savedUser.getEmail(), "1@test.com")))
                .andDo(print())
                .andDo(document("challenge-find-one",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(
                                removeHeaders("Vary", "X-Content-Type-Options", "X-XSS-Protection", "Pragma", "Expires",
                                        "Cache-Control", "Strict-Transport-Security", "X-Frame-Options"),
                                prettyPrint()),
                        pathParameters(
                                parameterWithName("challengeId").description("찾고 싶은 챌린지 ID")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("responseChallenge").description("챌린지 정보"),
                                fieldWithPath("responseUserChallenges").description("찾은 챌린지에 참여한 사람들 정보")
                        )
                ));
    }

    @Transactional
    @Test
    @DisplayName("모든 챌린지 조회하고 인기순으로 내림차순 정렬 테스트")
    public void searchAllChallengesSortByPopularTest() throws Exception {
        mockMvc.perform(get("/challenge")
                        .with(userRequestPostProcessor)
                        .param("size", "20")
                        .param("page", "0")
                        .param("sort", "popular")
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
                        hasItem(savedUser.getId().intValue())))
                .andDo(document("challenges-find-all-sort-by-popular",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(
                                removeHeaders("Vary", "X-Content-Type-Options", "X-XSS-Protection", "Pragma", "Expires",
                                        "Cache-Control", "Strict-Transport-Security", "X-Frame-Options"),
                                prettyPrint()),
                        requestParameters(
                                parameterWithName("size").description("기본값: 10").optional(),
                                parameterWithName("page").description("기본값: 0, 0번부터 시작합니다.").optional(),
                                parameterWithName("sort").description("기본값: popular-내림차순, popular 또는 time으로 정렬합니다.").optional()
                        ),
                        relaxedResponseFields(
                                fieldWithPath("totalElements").description("DB에 있는 전체 Challenge 개수"),
                                fieldWithPath("totalPages").description("만들 수 있는 page 개수")
//                                subsectionWithPath("content").description("Challenge 데이터"),
//                                subsectionWithPath("pageable").description("페이징 정보"),
//                                subsectionWithPath("last").description("마지막 페이지인지"),
//                                subsectionWithPath("size").description("페이지당 나타낼수 있는 데이터 개수"),
//                                subsectionWithPath("number").description("현재 페이지 번호"),
//                                subsectionWithPath("first").description("첫번쨰 페이지 인지"),
//                                subsectionWithPath("sort").description("정렬 정보"),
//                                subsectionWithPath("numberOfElements").description("실제 데이터 개수 "),
//                                subsectionWithPath("empty").description("리스트가 비어있는지 여부")
                        )
                ));
    }

    @Transactional
    @Test
    @DisplayName("모든 챌린지 조회하고 생성순으로 오름차순 정렬 테스트")
    public void searchAllChallengesSortByTimeTest() throws Exception {
        mockMvc.perform(get("/challenge")
                        .with(userRequestPostProcessor)
                        .param("size", "20")
                        .param("page", "0")
                        .param("sort", "time")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].title", contains(
                        "제목입니다.1", "제목입니다.2", "제목입니다.3", "제목입니다.4", "제목입니다.5",
                        "제목입니다.6", "제목입니다.7", "제목입니다.8", "제목입니다.9", "제목입니다.10")))
                .andDo(document("challenges-find-all-sort-by-time",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(
                                removeHeaders("Vary", "X-Content-Type-Options", "X-XSS-Protection", "Pragma", "Expires",
                                        "Cache-Control", "Strict-Transport-Security", "X-Frame-Options"),
                                prettyPrint()),
                        requestParameters(
                                parameterWithName("size").description("기본값: 10").optional(),
                                parameterWithName("page").description("기본값: 0, 0번부터 시작합니다.").optional(),
                                parameterWithName("sort").description("기본값: popular-내림차순, popular 또는 time으로 정렬합니다.")
                                        .optional()
                        ),
                        relaxedResponseFields(
                                fieldWithPath("totalElements").description("DB에 있는 전체 Challenge 개수"),
                                fieldWithPath("totalPages").description("만들 수 있는 page 개수")
                        )
                ));
    }

    @Transactional
    @Test
    @DisplayName("챌린지들을 검색 조건으로 조회하고 인기순으로 오름차순 정렬 테스트")
    public void searchChallengesByConditionSortByPopularTest() throws Exception {
        mockMvc.perform(get("/challenge/condition")
                        .with(userRequestPostProcessor)
                        .param("title", "")
                        .param("category", ChallengeCategory.WORKOUT.getDescription())
                        .param("size", "20")
                        .param("page", "0")
                        .param("sort", "popular")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].title",
                        contains("제목입니다.6", "제목입니다.3", "제목입니다.4", "제목입니다.5", "제목입니다.7", "제목입니다.8",
                                "제목입니다.9", "제목입니다.10")))
                .andExpect(jsonPath("$.content[*].content",
                        contains("내용입니다.6", "내용입니다.3", "내용입니다.4", "내용입니다.5", "내용입니다.7", "내용입니다.8",
                                "내용입니다.9", "내용입니다.10")))
                .andExpect(jsonPath("$.content[*].challengeCategory",
                        hasItem(ChallengeCategory.WORKOUT.getDescription())))
                .andExpect(jsonPath("$.content[*].challengeLocation",
                        hasItem(ChallengeLocation.INDOOR.getDescription())))
                .andExpect(jsonPath("$.content[*].challengeDuration",
                        hasItem(ChallengeDuration.WITHIN_TEN_MINUTES.getDescription())))
                .andExpect(jsonPath("$.content[*].challengeImgUrls",
                        hasItem(hasItem(startsWith("/images/")))))
                .andExpect(jsonPath("$.content[*].howManyUsersAreInThisChallenge",
                        contains(2, 1, 1, 1, 1, 1, 1, 1)))
                .andExpect(jsonPath("$.content[*].challengeOwnerUser.userName",
                        hasItem(savedUser.getUserName())))
                .andExpect(jsonPath("$.content[*].challengeOwnerUser.email",
                        hasItem(savedUser.getEmail())))
                .andExpect(jsonPath("$.content[*].challengeOwnerUser.userId",
                        hasItem(savedUser.getId().intValue())))
                .andDo(print())
                .andDo(document("challenges-find-by-condition-sort-by-popular",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(
                                removeHeaders("Vary", "X-Content-Type-Options", "X-XSS-Protection", "Pragma", "Expires",
                                        "Cache-Control", "Strict-Transport-Security", "X-Frame-Options"),
                                prettyPrint()),
                        requestParameters(
                                parameterWithName("title").description("찾고 싶은 Challenge 제목").optional(),
                                parameterWithName("category").description("찾고 싶은 Challenge 카테고리").optional(),
                                parameterWithName("size").description("기본값: 10").optional(),
                                parameterWithName("page").description("기본값: 0, 0번부터 시작합니다.").optional(),
                                parameterWithName("sort").description("기본값: popular-내림차순, popular 또는 time으로 정렬합니다.")
                                        .optional()
                        ),
                        relaxedResponseFields(
                                fieldWithPath("totalElements").description("DB에 있는 전체 Challenge 개수"),
                                fieldWithPath("totalPages").description("만들 수 있는 page 개수")
                        )
                ));
    }

    @Transactional
    @Test
    @DisplayName("챌린지들을 검색 조건으로 조회하고 생성순으로 내림차순 정렬 테스트")
    public void searchChallengesByConditionSortByTimeTest() throws Exception {
        mockMvc.perform(get("/challenge/condition")
                        .with(userRequestPostProcessor)
                        .param("title", "")
                        .param("category", ChallengeCategory.WORKOUT.getDescription())
                        .param("size", "20")
                        .param("page", "0")
                        .param("sort", "time")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].title",
                        contains("제목입니다.3", "제목입니다.4", "제목입니다.5","제목입니다.6", "제목입니다.7", "제목입니다.8",
                                "제목입니다.9", "제목입니다.10")))
                .andDo(print())
                .andDo(document("challenges-find-by-condition-sort-by-time",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(
                                removeHeaders("Vary", "X-Content-Type-Options", "X-XSS-Protection", "Pragma", "Expires",
                                        "Cache-Control", "Strict-Transport-Security", "X-Frame-Options"),
                                prettyPrint()),
                        requestParameters(
                                parameterWithName("title").description("찾고 싶은 Challenge 제목").optional(),
                                parameterWithName("category").description("찾고 싶은 Challenge 카테고리").optional(),
                                parameterWithName("size").description("기본값: 10").optional(),
                                parameterWithName("page").description("기본값: 0, 0번부터 시작합니다.").optional(),
                                parameterWithName("sort").description("기본값: popular-내림차순, popular 또는 time으로 정렬합니다.")
                                        .optional()
                        ),
                        relaxedResponseFields(
                                fieldWithPath("totalElements").description("DB에 있는 전체 Challenge 개수"),
                                fieldWithPath("totalPages").description("만들 수 있는 page 개수")
                        )
                ));
    }

    @Test
    @DisplayName("챌린지 삭제 테스트")
    void deleteChallenge() throws Exception {
        mockMvc.perform(delete("/challenge/{challengeId}", challenge1.getId())
                        .with(userRequestPostProcessor)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(print())
                .andDo(document("challenge-delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(
                                removeHeaders("Vary", "X-Content-Type-Options", "X-XSS-Protection", "Pragma", "Expires",
                                        "Cache-Control", "Strict-Transport-Security", "X-Frame-Options"),
                                prettyPrint()),
                        pathParameters(
                                parameterWithName("challengeId").description("챌린지 ID")
                        )
                ));
    }
}
