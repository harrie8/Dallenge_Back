package com.example.dailychallenge.controller.challenge;

import static com.example.dailychallenge.util.fixture.ChallengeImgFixture.createChallengeImgFiles;
import static com.example.dailychallenge.util.fixture.ChallengeImgFixture.updateChallengeImgFiles;
import static com.example.dailychallenge.util.fixture.TokenFixture.AUTHORIZATION;
import static com.example.dailychallenge.util.fixture.UserFixture.createSpecificUserDto;
import static com.example.dailychallenge.util.fixture.UserFixture.createUser;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.dailychallenge.dto.ChallengeDto;
import com.example.dailychallenge.dto.HashtagDto;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.ChallengeCategory;
import com.example.dailychallenge.entity.challenge.ChallengeDuration;
import com.example.dailychallenge.entity.challenge.ChallengeLocation;
import com.example.dailychallenge.entity.challenge.ChallengeStatus;
import com.example.dailychallenge.entity.comment.Comment;
import com.example.dailychallenge.entity.hashtag.Hashtag;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.repository.CommentRepository;
import com.example.dailychallenge.repository.UserRepository;
import com.example.dailychallenge.service.challenge.ChallengeService;
import com.example.dailychallenge.service.challenge.UserChallengeService;
import com.example.dailychallenge.service.hashtag.ChallengeHashtagService;
import com.example.dailychallenge.service.hashtag.HashtagService;
import com.example.dailychallenge.service.users.UserService;
import com.example.dailychallenge.util.RestDocsTest;
import com.example.dailychallenge.vo.challenge.RequestCreateChallenge;
import com.example.dailychallenge.vo.challenge.RequestUpdateChallenge;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

public class ChallengeControllerDocTest extends RestDocsTest {
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
    private HashtagService hashtagService;
    @Autowired
    private ChallengeHashtagService challengeHashtagService;

    private User savedUser;
    private Challenge challenge1;

    @BeforeEach
    void beforeEach() throws Exception {
        initData();
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

        List<String> hashtagDto = List.of("tag1", "tag2");
        List<Hashtag> hashtags = hashtagService.saveHashtag(hashtagDto);
        challengeHashtagService.saveChallengeHashtag(challenge1, hashtags);

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
            User otherUser = userService.saveUser(
                    createSpecificUserDto("홍길동" + i, i + "@test.com"), passwordEncoder);
            if (i == 1) {
                userChallengeService.saveUserChallenge(challenge1, otherUser);
            }
            if (2 <= i && i <= 5) {
                userChallengeService.saveUserChallenge(challenge2, otherUser);
            }

            if (i == 6) {
                userChallengeService.saveUserChallenge(challenge6, otherUser);
            }
        }
    }

    @Test
    @DisplayName("챌린지 생성")
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
                        .header(AUTHORIZATION, generateToken(savedUser.getEmail(), userService))
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
                .andExpect(jsonPath("$.challengeOwnerUser.userName").value(savedUser.getUserName()))
                .andExpect(jsonPath("$.challengeOwnerUser.email").value(savedUser.getEmail()))
                .andExpect(jsonPath("$.challengeOwnerUser.userId").value(savedUser.getId()))
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
                        )
                ));
    }

    @Test
    @DisplayName("특정 챌린지 조회 테스트")
    void findChallengeByIdTest() throws Exception {
        Long challenge1Id = challenge1.getId();

        mockMvc.perform(get("/challenge/{challengeId}", challenge1Id)
                        .header(AUTHORIZATION, generateToken(savedUser.getEmail(), userService))
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
                .andDo(restDocs.document(
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
                        .header(AUTHORIZATION, generateToken(savedUser.getEmail(), userService))
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
                .andDo(restDocs.document(
                        requestParameters(
                                parameterWithName("size").description("기본값: 10").optional(),
                                parameterWithName("page").description("기본값: 0, 0번부터 시작합니다.").optional(),
                                parameterWithName("sort").description("기본값: popular-내림차순, popular 또는 time으로 정렬합니다.")
                                        .optional()
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

    @Test
    @DisplayName("모든 챌린지 조회하고 생성순으로 오름차순 정렬 테스트")
    public void searchAllChallengesSortByTimeTest() throws Exception {
        mockMvc.perform(get("/challenge")
                        .header(AUTHORIZATION, generateToken(savedUser.getEmail(), userService))
                        .param("size", "20")
                        .param("page", "0")
                        .param("sort", "time")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].title", contains(
                        "제목입니다.10", "제목입니다.9", "제목입니다.8", "제목입니다.7", "제목입니다.6", "제목입니다.5",
                        "제목입니다.4", "제목입니다.3", "제목입니다.2", "제목입니다.1")))
                .andDo(restDocs.document(
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

    @Test
    @DisplayName("챌린지들을 검색 조건으로 조회하고 인기순으로 오름차순 정렬 테스트")
    public void searchChallengesByConditionSortByPopularTest() throws Exception {
        mockMvc.perform(get("/challenge/condition")
                        .characterEncoding(UTF_8)
                        .header(AUTHORIZATION, generateToken(savedUser.getEmail(), userService))
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
                .andDo(restDocs.document(
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
    @DisplayName("챌린지들을 검색 조건으로 조회하고 생성순으로 내림차순 정렬 테스트")
    public void searchChallengesByConditionSortByTimeTest() throws Exception {
        mockMvc.perform(get("/challenge/condition")
                        .header(AUTHORIZATION, generateToken(savedUser.getEmail(), userService))
                        .param("title", "")
                        .param("category", ChallengeCategory.WORKOUT.getDescription())
                        .param("size", "20")
                        .param("page", "0")
                        .param("sort", "time")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].title",
                        contains("제목입니다.10", "제목입니다.9", "제목입니다.8", "제목입니다.7", "제목입니다.6", "제목입니다.5",
                                "제목입니다.4", "제목입니다.3")))
                .andDo(restDocs.document(
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

        HashtagDto hashtagDto = HashtagDto.builder()
                .content(List.of("editTag1", "editTag2"))
                .build();
        String hashtagDtoJson = objectMapper.writeValueAsString(hashtagDto);
        MockMultipartFile mockHashtagDto = new MockMultipartFile("hashtagDto",
                "hashtagDto",
                "application/json", hashtagDtoJson.getBytes(UTF_8));

        String challengeCategoryDescriptions = String.join(", ", ChallengeCategory.getDescriptions());
        Long challenge1Id = challenge1.getId();
        mockMvc.perform(multipart("/challenge/{challengeId}", challenge1Id)
                        .file(mockRequestUpdateChallenge)
                        .part(new MockPart("updateChallengeImgFiles", "updateChallengeImgFiles",
                                updateChallengeImgFiles.get(0).getBytes()))
                        .part(new MockPart("updateChallengeImgFiles", "updateChallengeImgFiles",
                                updateChallengeImgFiles.get(1).getBytes()))
                        .file(mockHashtagDto)
                        .header(AUTHORIZATION, generateToken(savedUser.getEmail(), userService))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value(requestUpdateChallenge.getTitle()))
                .andExpect(jsonPath("$.content").value(requestUpdateChallenge.getContent()))
                .andExpect(jsonPath("$.challengeCategory").value(requestUpdateChallenge.getChallengeCategory()))
                .andExpect(jsonPath("$.challengeLocation").value(challenge1.getChallengeLocation().getDescription()))
                .andExpect(jsonPath("$.challengeDuration").value(challenge1.getChallengeDuration().getDescription()))
                .andExpect(jsonPath("$.created_at").value(challenge1.getFormattedCreatedAt()))
                .andExpect(jsonPath("$.updated_at").isNotEmpty())
                .andExpect(jsonPath("$.challengeImgUrls[*]", hasItem(startsWith("/images/"))))
                .andExpect(jsonPath("$.challengeHashtags[*]", contains("editTag1", "editTag2")))
                .andExpect(jsonPath("$.challengeImgUrls", hasSize(2)))
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("challengeId").description("수정하고 싶은 챌린지 ID")
                        ),
                        requestParts(
                                partWithName("requestUpdateChallenge").description("챌린지 데이터(JSON)")
                                        .attributes(key("type").value("JSON")),
                                partWithName("updateChallengeImgFiles").description("챌린지 이미지 파일들(FILE)").optional()
                                        .attributes(key("type").value(".jpg")),
                                partWithName("hashtagDto").description("해시태그 데이터(JSON)").optional()
                                        .attributes(key("type").value("JSON"))
                        ),
                        requestPartFields("requestUpdateChallenge",
                                fieldWithPath("title").description("제목"),
                                fieldWithPath("content").description("내용"),
                                fieldWithPath("challengeCategory").description("카테고리")
                                        .attributes(key("format").value(challengeCategoryDescriptions))
                        ),
                        requestPartFields("hashtagDto",
                                fieldWithPath("content").description("해시태그 내용")
                                        .attributes(key("format").value("\"\", \" \"은 허용하지 않습니다."))
                        ),
                        responseFields(
                                fieldWithPath("id").description("챌린지 ID"),
                                fieldWithPath("title").description("제목"),
                                fieldWithPath("content").description("내용"),
                                fieldWithPath("challengeCategory").description("카테고리"),
                                fieldWithPath("challengeLocation").description("장소"),
                                fieldWithPath("challengeDuration").description("기간"),
                                fieldWithPath("created_at").description("생성일"),
                                fieldWithPath("updated_at").description("수정일"),
                                fieldWithPath("challengeImgUrls").description("사진 url들"),
                                fieldWithPath("challengeHashtags").description("해시태그들")
                        )
                ));
    }

    @Test
    @DisplayName("챌린지 삭제 테스트")
    void deleteChallenge() throws Exception {
        mockMvc.perform(delete("/challenge/{challengeId}", challenge1.getId())
                        .header(AUTHORIZATION, generateToken(savedUser.getEmail(), userService))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(restDocs.document(
                        pathParameters(parameterWithName("challengeId").description("챌린지 ID"))));
    }
}

