package com.example.dailychallenge.service.bookmark;

import static com.example.dailychallenge.util.fixture.challenge.ChallengeFixture.createChallengeDto;
import static com.example.dailychallenge.util.fixture.challenge.ChallengeImgFixture.createChallengeImgFiles;
import static com.example.dailychallenge.util.fixture.user.UserFixture.createOtherUser;
import static com.example.dailychallenge.util.fixture.user.UserFixture.createUser;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.dailychallenge.entity.bookmark.Bookmark;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.exception.AuthorizationException;
import com.example.dailychallenge.exception.bookmark.BookmarkDuplicate;
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

    @Nested
    @DisplayName("북마크 생성 테스트")
    class saveBookmark {
        @Test
        void success() throws Exception {
            User otherUser = userService.saveUser(createOtherUser(), passwordEncoder);

            Bookmark savedBookmark = bookmarkService.saveBookmark(otherUser, challenge);

            assertAll(() -> {
                assertEquals(otherUser, savedBookmark.getUsers());
                assertEquals(challenge, savedBookmark.getChallenge());
            });
        }

        @Test
        @DisplayName("이미 챌린지를 북마크한 경우 예외 발생")
        void failByDuplication() throws Exception {
            User otherUser = userService.saveUser(createOtherUser(), passwordEncoder);
            bookmarkService.saveBookmark(otherUser, challenge);

            Throwable exception = assertThrows(BookmarkDuplicate.class,
                    () -> bookmarkService.saveBookmark(otherUser, challenge));
            assertEquals("이미 북마크한 챌린지입니다.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("북마크 삭제 테스트")
    class deleteBookmark {
        @Test
        void success() throws Exception {
            User otherUser = userService.saveUser(createOtherUser(), passwordEncoder);
            Bookmark savedBookmark = bookmarkService.saveBookmark(otherUser, challenge);
            Long bookmarkId = savedBookmark.getId();
            Long otherUserId = otherUser.getId();

            bookmarkService.deleteBookmark(otherUserId, bookmarkId);

            assertTrue(bookmarkRepository.findById(bookmarkId).isEmpty());
        }

        @Test
        @DisplayName("존재하지 않는 북마크를 삭제하는 테스트")
        void failByBookmarkNotFound() throws Exception {
            User otherUser = userService.saveUser(createOtherUser(), passwordEncoder);
            Bookmark savedBookmark = bookmarkService.saveBookmark(otherUser, challenge);
            Long notFoundBookmarkId = savedBookmark.getId() + 100L;
            Long otherUserId = otherUser.getId();

            Throwable exception = assertThrows(BookmarkNotFound.class,
                    () -> bookmarkService.deleteBookmark(otherUserId, notFoundBookmarkId));
            assertEquals("북마크를 찾을 수 없습니다.", exception.getMessage());
        }
    }

    @Nested
    @DisplayName("북마크한 회원인지 확인하는 테스트")
    class validateOwner {
        @Test
        void success() throws Exception {
            User otherUser = userService.saveUser(createOtherUser(), passwordEncoder);
            Bookmark savedBookmark = bookmarkService.saveBookmark(otherUser, challenge);
            Long otherUserId = otherUser.getId();

            assertDoesNotThrow(() -> bookmarkService.validateOwner(otherUserId, savedBookmark));
        }

        @Test
        @DisplayName("북마크한 회원이 아닌 경우 예외 발생")
        void failByAuthorization() throws Exception {
            User otherUser = userService.saveUser(createOtherUser(), passwordEncoder);
            Bookmark savedBookmark = bookmarkService.saveBookmark(otherUser, challenge);
            Long bookmarkId = savedBookmark.getId();
            Long savedUserId = savedUser.getId();

            Throwable exception = assertThrows(AuthorizationException.class,
                    () -> bookmarkService.deleteBookmark(savedUserId, bookmarkId));
            assertEquals("권한이 없습니다.", exception.getMessage());
        }
    }
}