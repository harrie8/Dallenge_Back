package com.example.dailychallenge.service.bookmark;

import com.example.dailychallenge.entity.bookmark.Bookmark;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.exception.bookmark.BookmarkDuplicate;
import com.example.dailychallenge.exception.bookmark.BookmarkNotFound;
import com.example.dailychallenge.repository.bookmark.BookmarkRepository;
import com.example.dailychallenge.vo.bookmark.ResponseBookmark;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class BookmarkService {
    private final BookmarkRepository bookmarkRepository;

    public Bookmark saveBookmark(User user, Challenge challenge) {
        Long userId = user.getId();
        Long challengeId = challenge.getId();
        bookmarkRepository.findByUserIdAndChallengeId(userId, challengeId).ifPresent(bookmark -> {
            throw new BookmarkDuplicate();
        });

        Bookmark bookmark = Bookmark.builder()
                .users(user)
                .challenge(challenge)
                .build();

        bookmarkRepository.save(bookmark);

        return bookmark;
    }

    public void deleteBookmark(Long bookmarkId) {
        Bookmark findBookmark = bookmarkRepository.findById(bookmarkId).orElseThrow(BookmarkNotFound::new);
        bookmarkRepository.delete(findBookmark);
    }

    public Page<ResponseBookmark> searchBookmarksByUserId(Long userId, Pageable pageable) {
        return bookmarkRepository.searchBookmarksByUserId(userId, pageable);
    }
}
