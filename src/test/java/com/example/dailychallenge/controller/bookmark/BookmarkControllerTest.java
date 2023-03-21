package com.example.dailychallenge.controller.bookmark;

import static com.example.dailychallenge.util.fixture.TokenFixture.EMAIL;
import static com.example.dailychallenge.util.fixture.TokenFixture.PASSWORD;
import static com.example.dailychallenge.util.fixture.challenge.ChallengeFixture.createChallengeDto;
import static com.example.dailychallenge.util.fixture.user.UserFixture.OTHER_EMAIL;
import static com.example.dailychallenge.util.fixture.user.UserFixture.OTHER_USERNAME;
import static com.example.dailychallenge.util.fixture.user.UserFixture.USERNAME;
import static com.example.dailychallenge.util.fixture.user.UserFixture.getRequestPostProcessor;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.dailychallenge.entity.bookmark.Bookmark;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.service.bookmark.BookmarkService;
import com.example.dailychallenge.util.ControllerTest;
import com.example.dailychallenge.util.fixture.TestDataSetup;
import java.util.ArrayList;
import java.util.List;
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
    private TestDataSetup testDataSetup;

    private User user;
    private Challenge challenge;
    private RequestPostProcessor requestPostProcessor;

    @BeforeEach
    void beforeEach() {
        user = testDataSetup.saveUser(USERNAME, EMAIL, PASSWORD);
        challenge = testDataSetup.챌린지를_생성한다(createChallengeDto(), user);
        testDataSetup.챌린지에_참가한다(challenge, user);
        requestPostProcessor = getRequestPostProcessor(user);
    }

    @Test
    @DisplayName("북마크 생성 테스트")
    public void createBookmarkTest() throws Exception {
        User otherUser = testDataSetup.saveUser(OTHER_USERNAME, OTHER_EMAIL, PASSWORD);

        Long challengeId = challenge.getId();
        mockMvc.perform(post("/{challengeId}/bookmark/new", challengeId)
                        .with(getRequestPostProcessor(otherUser))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.title").value(challenge.getTitle()))
                .andExpect(jsonPath("$.createdAt").isNotEmpty())
                .andExpect(jsonPath("$.userId").value(otherUser.getId()))
                .andExpect(jsonPath("$.challengeId").value(challenge.getId()))
                .andExpect(jsonPath("$.challengeContent").value(challenge.getContent()))
                .andExpect(jsonPath("$.challengeImgUrls").value(challenge.getImgUrls()));
    }

    @Test
    @DisplayName("북마크 삭제 테스트")
    public void deleteBookmarkTest() throws Exception {
        Bookmark savedBookmark = bookmarkService.saveBookmark(user, challenge);
        Long userId = user.getId();
        Long bookmarkId = savedBookmark.getId();

        mockMvc.perform(delete("/user/{userId}/bookmark/{bookmarkId}", userId, bookmarkId)
                        .with(requestPostProcessor)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("유저의 북마크들 조회 테스트")
    public void searchBookmarksByUserIdTest() throws Exception {
        User otherUser = testDataSetup.saveUser(OTHER_USERNAME, OTHER_EMAIL, PASSWORD);

        List<Challenge> otherChallenges = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Challenge otherChallenge = testDataSetup.챌린지를_생성한다(createChallengeDto(), user);
            testDataSetup.챌린지에_참가한다(otherChallenge, user);
            otherChallenges.add(otherChallenge);

            Thread.sleep(1);
            bookmarkService.saveBookmark(otherUser, otherChallenge);
        }
        Long otherUserId = otherUser.getId();

        mockMvc.perform(get("/user/{userId}/bookmark", otherUserId)
                        .with(getRequestPostProcessor(otherUser))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(5)))
                .andExpect(jsonPath("$.content[*].id").isNotEmpty())
                .andExpect(jsonPath("$.content[*].title",
                        hasItems("제목입니다.")))
                .andExpect(jsonPath("$.content[*].createdAt").isNotEmpty())
                .andExpect(jsonPath("$.content[*].userId",
                        hasItems(otherUser.getId().intValue())))
                .andExpect(jsonPath("$.content[*].challengeId",
                        hasItems(otherChallenges.get(4).getId().intValue(), otherChallenges.get(3).getId().intValue(),
                                otherChallenges.get(2).getId().intValue(), otherChallenges.get(1).getId().intValue(),
                                otherChallenges.get(0).getId().intValue())))
                .andExpect(jsonPath("$.content[*].challengeContent",
                        hasItems(otherChallenges.get(4).getContent(), otherChallenges.get(3).getContent(),
                                otherChallenges.get(2).getContent(), otherChallenges.get(1).getContent(),
                                otherChallenges.get(0).getContent())))
                .andExpect(jsonPath("$.content[*].challengeImgUrls",
                        hasItems(otherChallenges.get(4).getImgUrls(), otherChallenges.get(3).getImgUrls(),
                                otherChallenges.get(2).getImgUrls(), otherChallenges.get(1).getImgUrls(),
                                otherChallenges.get(0).getImgUrls())));
    }
}