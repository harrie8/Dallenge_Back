package com.example.dailychallenge.controller.bookmark;

import static com.example.dailychallenge.util.fixture.ChallengeFixture.createChallengeDto;
import static com.example.dailychallenge.util.fixture.ChallengeImgFixture.createChallengeImgFiles;
import static com.example.dailychallenge.util.fixture.UserFixture.createOtherUser;
import static com.example.dailychallenge.util.fixture.UserFixture.createUser;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.service.challenge.ChallengeService;
import com.example.dailychallenge.service.users.UserService;
import com.example.dailychallenge.util.ControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

class BookmarkControllerTest extends ControllerTest {
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
    public void createBookmarkTest() throws Exception {
        User otherUser = userService.saveUser(createOtherUser(), passwordEncoder);
        requestPostProcessor = user(userService.loadUserByUsername(otherUser.getEmail()));

        Long challengeId = challenge.getId();
        mockMvc.perform(post("/{challengeId}/bookmark/new", challengeId)
                        .with(requestPostProcessor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(challenge.getTitle()))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.userId").value(otherUser.getId()));
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
//        String token = generateToken();
//        mockMvc.perform(multipart("/{challengeId}/comment/{commentId}",challengeId,savedComment.getId())
//                        .file(commentDto)
//                        .file(commentDtoImg)
//                        .header(AUTHORIZATION, token)
//                        .contentType(MediaType.MULTIPART_FORM_DATA)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(print());
//    }
//
//    @Test
//    @DisplayName("댓글 삭제 테스트")
//    public void deleteCommentTest() throws Exception {
//        Comment savedComment = createComment();
//        Long challengeId = savedComment.getChallenge().getId();
//        String token = generateToken();
//        mockMvc.perform(delete("/{challengeId}/comment/{commentId}",challengeId,savedComment.getId())
//                        .header(AUTHORIZATION, token)
//                        .contentType(MediaType.MULTIPART_FORM_DATA)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(print());
//    }
//
//    @Test
//    @DisplayName("좋아요 테스트")
//    public void isLikeTest() throws Exception {
//        Comment savedComment = createComment();
//
//        String token = generateToken();
//        mockMvc.perform(post("/{commentId}/like", savedComment.getId())
//                        .param("isLike", String.valueOf(1))
//                        .header(AUTHORIZATION, token)
//                        .contentType(MediaType.MULTIPART_FORM_DATA)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(print());
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
//                .andExpect(jsonPath("$.content", hasSize(5)))
//                .andExpect(jsonPath("$.content[*].id").isNotEmpty())
//                .andExpect(jsonPath("$.content[*].content", hasItems(startsWith("댓글 내용"))))
//                .andExpect(jsonPath("$.content[*].likes", hasItems(0)))
//                .andExpect(jsonPath("$.content[*].createdAt").isNotEmpty())
//                .andExpect(jsonPath("$.content[*].commentImgUrls",
//                        hasItems(hasItem(startsWith("/images/")))))
//                .andExpect(jsonPath("$.content[*].commentOwnerUser.userName",
//                        hasItems(otherUser.getUserName())))
//                .andExpect(jsonPath("$.content[*].commentOwnerUser.email",
//                        hasItems(otherUser.getEmail())))
//                .andExpect(jsonPath("$.content[*].commentOwnerUser.userId",
//                        hasItems(otherUser.getId().intValue())))
//                .andDo(print());
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
//                .andExpect(jsonPath("$.content", hasSize(8)))
//                .andExpect(jsonPath("$.content[*].id").isNotEmpty())
//                .andExpect(jsonPath("$.content[*].content",
//                        contains("다른 댓글 내용7", "다른 댓글 내용6", "다른 댓글 내용5", "댓글 내용4",
//                                "댓글 내용3", "댓글 내용2", "댓글 내용1", "댓글 내용0")))
//                .andExpect(jsonPath("$.content[*].likes", hasItems(0)))
//                .andExpect(jsonPath("$.content[*].createdAt").isNotEmpty())
//                .andExpect(jsonPath("$.content[*].commentImgUrls",
//                        hasItems(hasItem(startsWith("/images/")))))
//                .andExpect(jsonPath("$.content[*].challengeId",
//                        hasItems(challenge.getId().intValue(), otherChallenge.getId().intValue())))
//                .andExpect(jsonPath("$.content[*].challengeTitle",
//                        hasItems(challenge.getTitle(), otherChallenge.getTitle())))
//                .andDo(print());
//    }
}