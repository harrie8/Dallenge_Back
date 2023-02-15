package com.example.dailychallenge.controller.bookmark;

import static com.example.dailychallenge.util.fixture.ChallengeFixture.createChallengeDto;
import static com.example.dailychallenge.util.fixture.ChallengeImgFixture.createChallengeImgFiles;
import static com.example.dailychallenge.util.fixture.UserFixture.createOtherUser;
import static com.example.dailychallenge.util.fixture.UserFixture.createUser;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.service.challenge.ChallengeService;
import com.example.dailychallenge.service.users.UserService;
import com.example.dailychallenge.util.RestDocsTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

public class BookmarkControllerDocTest  extends RestDocsTest {
    @Autowired
    private UserService userService;
    @Autowired
    private ChallengeService challengeService;

    private User savedUser;
    private Challenge challenge;
    private RequestPostProcessor requestPostProcessor;

    @BeforeEach
    void beforeEach() throws Exception {
        initData();
        requestPostProcessor = user(userService.loadUserByUsername(savedUser.getEmail()));
    }

    private void initData() throws Exception {
        savedUser = userService.saveUser(createUser(), passwordEncoder);
        challenge = challengeService.saveChallenge(createChallengeDto(), createChallengeImgFiles(), savedUser);
    }

    @Test
    @DisplayName("북마크 생성 테스트")
    public void createBookmark() throws Exception {
        User otherUser = userService.saveUser(createOtherUser(), passwordEncoder);
        requestPostProcessor = user(userService.loadUserByUsername(otherUser.getEmail()));

        Long challengeId = challenge.getId();
        mockMvc.perform(post("/{challengeId}/bookmark/new", challengeId)
                        .with(requestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("challengeId").description("챌린지 아이디")
                        ),
                        responseFields(
                                fieldWithPath("title").description("챌린지 제목"),
                                fieldWithPath("createdAt").description("북마크 생성 시간"),
                                fieldWithPath("userId").description("회원 ID")
                        )
                ));
    }

//    @Test
//    @DisplayName("댓글 수정 테스트")
//    public void updateCommentTest() throws Exception {
//        Comment savedComment = createComment();
//
//        CommentDto requestComment = CommentDto.builder()
//                .content("댓글 수정")
//                .build();
//        MockMultipartFile commentDtoImg = createMultipartFiles();
//
//        String json = objectMapper.writeValueAsString(requestComment);
//        MockMultipartFile commentDto = new MockMultipartFile("commentDto",
//                "commentDto",
//                "application/json", json.getBytes(StandardCharsets.UTF_8));
//
//        Long challengeId = savedComment.getChallenge().getId();
//        Long commentId = savedComment.getId();
//        String token = generateToken();
//        mockMvc.perform(RestDocumentationRequestBuilders
//                        .multipart("/{challengeId}/comment/{commentId}",challengeId,commentId)
//                        .file(commentDto)
//                        .file(commentDtoImg)
//                        .header(AUTHORIZATION, token)
//                        .contentType(MediaType.MULTIPART_FORM_DATA)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(print())
//                .andDo(document("comment-update",
//                        preprocessRequest(prettyPrint()),
//                        preprocessResponse(
//                                removeHeaders("Vary", "X-Content-Type-Options", "X-XSS-Protection", "Pragma", "Expires",
//                                        "Cache-Control", "Strict-Transport-Security", "X-Frame-Options"),
//                                prettyPrint()),
//                        pathParameters(
//                                parameterWithName("challengeId").description("챌린지 아이디"),
//                                parameterWithName("commentId").description("댓글 아이디")
//                        ),
//                        requestParts(
//                                partWithName("commentDto").description("댓글 수정 정보 데이터(JSON)").attributes(key("type").value("JSON")),
//                                partWithName("commentDtoImg").description("댓글 수정 이미지 파일(FILE)").optional().attributes(key("type").value(".jpg"))
//                        ),
//                        requestPartFields("commentDto",
//                                fieldWithPath("content").description("수정할 내용")
//                        )
//                ));
//    }
//
//    @Test
//    @DisplayName("댓글 삭제 테스트")
//    public void deleteCommentTest() throws Exception {
//        Comment savedComment = createComment();
//        Long challengeId = savedComment.getChallenge().getId();
//        String token = generateToken();
//        mockMvc.perform(RestDocumentationRequestBuilders
//                        .delete("/{challengeId}/comment/{commentId}",challengeId,savedComment.getId())
//                        .header(AUTHORIZATION, token)
//                        .contentType(MediaType.MULTIPART_FORM_DATA)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(print())
//                .andDo(document("comment-delete",
//                        preprocessRequest(prettyPrint()),
//                        preprocessResponse(
//                                removeHeaders("Vary", "X-Content-Type-Options", "X-XSS-Protection", "Pragma", "Expires",
//                                        "Cache-Control", "Strict-Transport-Security", "X-Frame-Options"),
//                                prettyPrint()),
//                        pathParameters(
//                                parameterWithName("challengeId").description("챌린지 아이디"),
//                                parameterWithName("commentId").description("댓글 아이디")
//                        )
//                ));
//    }
//
//    @Test
//    @DisplayName("좋아요 테스트")
//    public void isLikeTest() throws Exception {
//        Comment savedComment = createComment();
//        Integer beforeLikes = savedComment.getLikes();
//        String token = generateToken();
//        mockMvc.perform(RestDocumentationRequestBuilders
//                        .post("/{commentId}/like?isLike=1", savedComment.getId())
//                        .header(AUTHORIZATION, token)
//                        .contentType(MediaType.MULTIPART_FORM_DATA)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.isLike").value(beforeLikes +1))
//                .andDo(document("comment-like",
//                        preprocessRequest(prettyPrint()),
//                        preprocessResponse(
//                                removeHeaders("Vary", "X-Content-Type-Options", "X-XSS-Protection", "Pragma", "Expires",
//                                        "Cache-Control", "Strict-Transport-Security", "X-Frame-Options"),
//                                prettyPrint()),
//                        requestParameters(
//                                parameterWithName("isLike").description("좋아요(1)/좋아요 취소(0)")
//                        ),
//                        pathParameters(
//                                parameterWithName("commentId").description("댓글 아이디")
//                        )
//                ));
//    }
//
//    @Test
//    @DisplayName("특정 챌린지의 댓글들 조회 테스트")
//    public void searchCommentsByChallengeId() throws Exception {
//        Challenge challenge = createChallenge();
//        User otherUser = userService.saveUser(createOtherUser(), passwordEncoder);
//        for (int i = 0; i < 5; i++) {
//            CommentDto commentDto = CommentDto.builder()
//                    .content("댓글 내용" + i)
//                    .build();
//            List<MultipartFile> commentDtoImg = new ArrayList<>();
//            commentDtoImg.add(createMultipartFiles());
//            commentService.saveComment(commentDto, otherUser, challenge, commentDtoImg);
//        }
//        Long challengeId = challenge.getId();
//
//        mockMvc.perform(get("/{challengeId}/comment", challengeId)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(document("search-comments-by-challengeId",
//                        preprocessRequest(prettyPrint()),
//                        preprocessResponse(
//                                removeHeaders("Vary", "X-Content-Type-Options", "X-XSS-Protection", "Pragma", "Expires",
//                                        "Cache-Control", "Strict-Transport-Security", "X-Frame-Options"),
//                                prettyPrint()),
//                        pathParameters(
//                                parameterWithName("challengeId").description("챌린지 아이디")
//                        ),
//                        relaxedResponseFields(
//                                fieldWithPath("content[*].id").description("댓글 id"),
//                                fieldWithPath("content[*].content").description("댓글 내용"),
//                                fieldWithPath("content[*].likes").description("댓글 좋아요 갯수"),
//                                fieldWithPath("content[*].createdAt").description("댓글 생성 시간"),
//                                fieldWithPath("content[*].commentImgUrls").description("댓글 이미지들 url"),
//                                fieldWithPath("content[*].commentOwnerUser").description("댓글 소유자 정보")
//                        )
//                ));
//    }
//
//    @Test
//    @DisplayName("유저가 작성한 챌린지의 댓글들 조회 테스트")
//    public void searchCommentsByUserId() throws Exception {
//        Challenge challenge = createChallenge();
//        User savedUser = challenge.getUsers();
//        for (int i = 0; i < 5; i++) {
//            CommentDto commentDto = CommentDto.builder()
//                    .content("댓글 내용" + i)
//                    .build();
//            List<MultipartFile> commentDtoImg = new ArrayList<>();
//            commentDtoImg.add(createMultipartFiles());
//            commentService.saveComment(commentDto, savedUser, challenge, commentDtoImg);
//        }
//        ChallengeDto challengeDto = ChallengeDto.builder()
//                .title("다른 제목입니다.")
//                .content("다른 내용입니다.")
//                .challengeCategory(ChallengeCategory.STUDY.getDescription())
//                .challengeLocation(ChallengeLocation.INDOOR.getDescription())
//                .challengeDuration(ChallengeDuration.WITHIN_TEN_MINUTES.getDescription())
//                .build();
//        MultipartFile challengeImg = createMultipartFiles();
//        List<MultipartFile> challengeImgFiles = List.of(challengeImg);
//        Challenge otherChallenge = challengeService.saveChallenge(challengeDto, challengeImgFiles, savedUser);
//        for (int i = 5; i < 8; i++) {
//            CommentDto commentDto = CommentDto.builder()
//                    .content("다른 댓글 내용" + i)
//                    .build();
//            List<MultipartFile> commentDtoImg = new ArrayList<>();
//            commentDtoImg.add(createMultipartFiles());
//            commentService.saveComment(commentDto, savedUser, otherChallenge, commentDtoImg);
//        }
//        Long userId = savedUser.getId();
//
//        mockMvc.perform(get("/user/{userId}/comment", userId)
//                        .with(user(userService.loadUserByUsername(savedUser.getEmail())))
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(print())
//                .andDo(document("search-comments-by-userId",
//                        preprocessRequest(prettyPrint()),
//                        preprocessResponse(
//                                removeHeaders("Vary", "X-Content-Type-Options", "X-XSS-Protection", "Pragma", "Expires",
//                                        "Cache-Control", "Strict-Transport-Security", "X-Frame-Options"),
//                                prettyPrint()),
//                        pathParameters(
//                                parameterWithName("userId").description("유저 아이디")
//                        ),
//                        relaxedResponseFields(
//                                fieldWithPath("content[*].id").description("댓글 id"),
//                                fieldWithPath("content[*].content").description("댓글 내용"),
//                                fieldWithPath("content[*].likes").description("댓글 좋아요 갯수"),
//                                fieldWithPath("content[*].createdAt").description("댓글 생성 시간"),
//                                fieldWithPath("content[*].commentImgUrls").description("댓글 이미지들 url"),
//                                fieldWithPath("content[*].challengeId").description("챌린지 id"),
//                                fieldWithPath("content[*].challengeTitle").description("챌린지 제목")
//                        )
//                ));
//    }
}
