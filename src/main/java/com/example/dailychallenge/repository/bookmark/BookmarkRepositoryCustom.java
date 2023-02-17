package com.example.dailychallenge.repository.bookmark;

import com.example.dailychallenge.entity.bookmark.Bookmark;
import com.example.dailychallenge.vo.bookmark.ResponseBookmark;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookmarkRepositoryCustom {
    Page<ResponseBookmark> searchBookmarksByUserId(Long userId, Pageable pageable);

    Optional<Bookmark> findByUserIdAndChallengeId(Long userId, Long challengeId);
}
