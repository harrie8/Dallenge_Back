package com.example.dailychallenge.repsoitory.bookmark;

import static com.example.dailychallenge.util.fixture.ChallengeFixture.createChallenge;
import static com.example.dailychallenge.util.fixture.TokenFixture.EMAIL;
import static com.example.dailychallenge.util.fixture.TokenFixture.PASSWORD;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.dailychallenge.entity.bookmark.Bookmark;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.repository.ChallengeRepository;
import com.example.dailychallenge.repository.UserRepository;
import com.example.dailychallenge.repository.bookmark.BookmarkRepository;
import com.example.dailychallenge.util.RepositoryTest;
import com.example.dailychallenge.vo.bookmark.ResponseBookmark;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

class BookmarkRepositoryCustomImplTest extends RepositoryTest {

    @Autowired
    private BookmarkRepository bookmarkRepository;
    @Autowired
    private ChallengeRepository challengeRepository;
    @Autowired
    private UserRepository userRepository;

    private User savedUser;
    private Challenge challenge;

    @BeforeEach
    void beforeEach() {
        savedUser = User.builder()
                .userName("홍길동")
                .email(EMAIL)
                .password(PASSWORD)
                .build();
        userRepository.save(savedUser);

        challenge = createChallenge();
        challenge.setUser(savedUser);
        challengeRepository.save(challenge);
    }

    @Test
    @DisplayName("유저의 북마크들을 조회하는 테스트")
    void searchBookmarksByUserIdTest() throws InterruptedException {
        User otherUser = User.builder()
                .userName("김철수")
                .email("a@a.com")
                .password(PASSWORD)
                .build();
        userRepository.save(otherUser);
        for (int i = 0; i < 5; i++) {
            challenge = createChallenge();
            challenge.setUser(savedUser);
            challengeRepository.save(challenge);

            Thread.sleep(1);
            Bookmark bookmark = Bookmark.builder()
                    .users(otherUser)
                    .challenge(challenge)
                    .build();
            bookmarkRepository.save(bookmark);
        }
        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("time").descending());

        Page<ResponseBookmark> results = bookmarkRepository.searchBookmarksByUserId(otherUser.getId(),
                pageRequest);

        assertAll(() -> {
            assertEquals(5L, results.getTotalElements());
            assertThat(results).extracting("id").isNotEmpty();
            assertThat(results).extracting("title").containsOnly(challenge.getTitle());
            assertThat(results).extracting("createdAt").isNotEmpty();
            assertThat(results).extracting("userId").containsOnly(otherUser.getId());
        });
    }
}