package com.example.dailychallenge.controller.bookmark;

import static com.example.dailychallenge.util.fixture.ChallengeFixture.createChallengeDto;
import static com.example.dailychallenge.util.fixture.ChallengeImgFixture.createChallengeImgFiles;
import static com.example.dailychallenge.util.fixture.UserFixture.createOtherUser;
import static com.example.dailychallenge.util.fixture.UserFixture.createUser;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.service.bookmark.BookmarkService;
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
    private BookmarkService bookmarkService;
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

    @Test
    @DisplayName("유저의 북마크들 조회 테스트")
    public void searchBookmarksByUserIdTest() throws Exception {
        User otherUser = userService.saveUser(createOtherUser(), passwordEncoder);
        for (int i = 0; i < 5; i++) {
            Challenge otherChallenge = challengeService.saveChallenge(createChallengeDto(), createChallengeImgFiles(),
                    savedUser);

            Thread.sleep(1);
            bookmarkService.saveBookmark(otherUser, otherChallenge);
        }
        Long otherUserId = otherUser.getId();

        mockMvc.perform(get("/user/{userId}/bookmark", otherUserId)
                        .with(user(userService.loadUserByUsername(otherUser.getEmail())))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)))
                .andExpect(jsonPath("$.content[*].title",
                        hasItems("제목입니다.")))
                .andExpect(jsonPath("$.content[*].createdAt").isNotEmpty())
                .andExpect(jsonPath("$.content[*].userId",
                        hasItems(otherUser.getId().intValue())));
    }
}