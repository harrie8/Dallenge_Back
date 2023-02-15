package com.example.dailychallenge.controller.bookmark;

import static com.example.dailychallenge.util.fixture.ChallengeFixture.createChallengeDto;
import static com.example.dailychallenge.util.fixture.ChallengeImgFixture.createChallengeImgFiles;
import static com.example.dailychallenge.util.fixture.UserFixture.createOtherUser;
import static com.example.dailychallenge.util.fixture.UserFixture.createUser;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.dailychallenge.entity.bookmark.Bookmark;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.service.bookmark.BookmarkService;
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

    @Test
    @DisplayName("북마크 삭제 테스트")
    public void deleteBookmark() throws Exception {
        Bookmark savedBookmark = bookmarkService.saveBookmark(savedUser, challenge);
        Long userId = savedUser.getId();
        Long bookmarkId = savedBookmark.getId();

        mockMvc.perform(delete("/user/{userId}/bookmark/{bookmarkId}", userId, bookmarkId)
                        .with(requestPostProcessor)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("userId").description("유저 ID"),
                                parameterWithName("bookmarkId").description("북마크 ID")
                        )
                ));
    }

    @Test
    @DisplayName("유저의 북마크들 조회 테스트")
    public void searchBookmarksByUserId() throws Exception {
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
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("userId").description("유저 ID")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("content[*].title").description("챌린지 제목"),
                                fieldWithPath("content[*].createdAt").description("북마크한 시간"),
                                fieldWithPath("content[*].userId").description("유저 id")
                        )
                ));
    }
}
