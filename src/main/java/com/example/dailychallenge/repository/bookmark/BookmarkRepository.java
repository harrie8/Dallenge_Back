package com.example.dailychallenge.repository.bookmark;

import com.example.dailychallenge.entity.bookmark.Bookmark;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
}
