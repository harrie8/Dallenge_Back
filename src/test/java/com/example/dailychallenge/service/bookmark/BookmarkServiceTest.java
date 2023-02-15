package com.example.dailychallenge.service.bookmark;

import static com.example.dailychallenge.util.fixture.ChallengeFixture.createChallengeDto;
import static com.example.dailychallenge.util.fixture.ChallengeImgFixture.createChallengeImgFiles;
import static com.example.dailychallenge.util.fixture.UserFixture.createOtherUser;
import static com.example.dailychallenge.util.fixture.UserFixture.createUser;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.dailychallenge.entity.bookmark.Bookmark;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.exception.bookmark.BookmarkNotFound;
import com.example.dailychallenge.repository.bookmark.BookmarkRepository;
import com.example.dailychallenge.service.challenge.ChallengeService;
import com.example.dailychallenge.service.users.UserService;
import com.example.dailychallenge.util.ServiceTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class BookmarkServiceTest extends ServiceTest {

    @Autowired
    private BookmarkService bookmarkService;
    @Autowired
    private UserService userService;
    @Autowired
    private ChallengeService challengeService;
    @Autowired
    private BookmarkRepository bookmarkRepository;

    private User savedUser;
    private Challenge challenge;

    @BeforeEach
    void beforeEach() throws Exception {
        savedUser = userService.saveUser(createUser(), passwordEncoder);
        challenge = challengeService.saveChallenge(createChallengeDto(), createChallengeImgFiles(), savedUser);
    }

    @Test
    @DisplayName("북마크 생성 테스트")
    void saveBookmarkTest() throws Exception {
        User otherUser = userService.saveUser(createOtherUser(), passwordEncoder);

        Bookmark savedBookmark = bookmarkService.saveBookmark(otherUser, challenge);

        assertAll(() -> {
            assertEquals(otherUser, savedBookmark.getUsers());
            assertEquals(challenge, savedBookmark.getChallenge());
        });
    }

    @Nested
    @DisplayName("북마크 삭제 테스트")
    class deleteBookmark {
        @Test
        void success() throws Exception {
            User otherUser = userService.saveUser(createOtherUser(), passwordEncoder);
            Bookmark savedBookmark = bookmarkService.saveBookmark(otherUser, challenge);
            Long bookmarkId = savedBookmark.getId();

            bookmarkService.deleteBookmark(bookmarkId);

            assertTrue(bookmarkRepository.findById(bookmarkId).isEmpty());
        }

        @Test
        @DisplayName("존재하지 않는 북마크를 삭제하는 테스트")
        void failByBookmarkNotFound() throws Exception {
            User otherUser = userService.saveUser(createOtherUser(), passwordEncoder);
            Bookmark savedBookmark = bookmarkService.saveBookmark(otherUser, challenge);
            Long notFoundBookmarkId = savedBookmark.getId() + 100L;

            Throwable exception = assertThrows(BookmarkNotFound.class,
                    () -> bookmarkService.deleteBookmark(notFoundBookmarkId));
            assertEquals("북마크를 찾을 수 없습니다.", exception.getMessage());
        }
    }
}