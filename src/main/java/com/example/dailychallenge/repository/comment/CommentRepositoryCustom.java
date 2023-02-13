package com.example.dailychallenge.repository.comment;

import com.example.dailychallenge.vo.ResponseChallengeComment;
import com.example.dailychallenge.vo.ResponseUserComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentRepositoryCustom {
    Page<ResponseChallengeComment> searchCommentsByChallengeId(Long challengeId, Pageable pageable);
    Page<ResponseUserComment> searchCommentsByUserId(Long userId, Pageable pageable);
}
