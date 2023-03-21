package com.example.dailychallenge.controller.bookmark;

import static com.example.dailychallenge.util.fixture.TokenFixture.AUTHORIZATION;
import static com.example.dailychallenge.util.fixture.TokenFixture.EMAIL;
import static com.example.dailychallenge.util.fixture.TokenFixture.PASSWORD;
import static com.example.dailychallenge.util.fixture.challenge.ChallengeFixture.createChallengeDto;
import static com.example.dailychallenge.util.fixture.user.UserFixture.OTHER_EMAIL;
import static com.example.dailychallenge.util.fixture.user.UserFixture.OTHER_USERNAME;
import static com.example.dailychallenge.util.fixture.user.UserFixture.USERNAME;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.dailychallenge.entity.bookmark.Bookmark;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.service.bookmark.BookmarkService;
import com.example.dailychallenge.util.RestDocsTest;
import com.example.dailychallenge.util.fixture.TestDataSetup;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;

public class BookmarkControllerDocTest extends RestDocsTest {
    @Autowired
    private BookmarkService bookmarkService;
    @Autowired
    private TestDataSetup testDataSetup;

    private User user;
    private Challenge challenge;
    private String token;

    @BeforeEach
    void beforeEach() {
        user = testDataSetup.saveUser(USERNAME, EMAIL, PASSWORD);
        challenge = testDataSetup.챌린지를_생성한다(createChallengeDto(), user);
        testDataSetup.챌린지에_참가한다(challenge, user);
        token = generateToken(user);
    }

    @Test
    @DisplayName("북마크 생성 테스트")
    public void createBookmark() throws Exception {
        User otherUser = testDataSetup.saveUser(OTHER_USERNAME, OTHER_EMAIL, PASSWORD);

        Long challengeId = challenge.getId();
        mockMvc.perform(post("/{challengeId}/bookmark/new", challengeId)
                        .header(AUTHORIZATION, generateToken(otherUser))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("challengeId").description("챌린지 아이디")
                        ),
                        responseFields(
                                fieldWithPath("id").description("북마크 ID"),
                                fieldWithPath("title").description("챌린지 제목"),
                                fieldWithPath("createdAt").description("북마크 생성 시간"),
                                fieldWithPath("userId").description("회원 ID"),
                                fieldWithPath("challengeId").description("챌린지 ID")
                        )
                ));
    }

    @Test
    @DisplayName("북마크 삭제 테스트")
    public void deleteBookmark() throws Exception {
        Bookmark savedBookmark = bookmarkService.saveBookmark(user, challenge);
        Long userId = user.getId();
        Long bookmarkId = savedBookmark.getId();

        mockMvc.perform(delete("/user/{userId}/bookmark/{bookmarkId}", userId, bookmarkId)
                        .header(AUTHORIZATION, token)
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
        User otherUser = testDataSetup.saveUser(OTHER_USERNAME, OTHER_EMAIL, PASSWORD);
        for (int i = 0; i < 5; i++) {
            Challenge otherChallenge = testDataSetup.챌린지를_생성한다(createChallengeDto(), user);

            Thread.sleep(1);
            bookmarkService.saveBookmark(otherUser, otherChallenge);
        }
        Long otherUserId = otherUser.getId();

        mockMvc.perform(get("/user/{userId}/bookmark", otherUserId)
                        .header(AUTHORIZATION, generateToken(otherUser))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(restDocs.document(
                        pathParameters(
                                parameterWithName("userId").description("유저 ID")
                        ),
                        relaxedResponseFields(
                                fieldWithPath("content[*].id").description("북마크 ID"),
                                fieldWithPath("content[*].title").description("챌린지 제목"),
                                fieldWithPath("content[*].createdAt").description("북마크한 시간"),
                                fieldWithPath("content[*].userId").description("유저 ID"),
                                fieldWithPath("content[*].challengeId").description("챌린지 ID"),
                                fieldWithPath("content[*].challengeContent").description("챌린지 내용"),
                                fieldWithPath("content[*].challengeImgUrls").description("챌린지 이미지들")
                        )
                ));
    }
}
