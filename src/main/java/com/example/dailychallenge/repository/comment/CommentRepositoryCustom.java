package com.example.dailychallenge.repository.comment;

import com.example.dailychallenge.vo.ResponseChallengeComment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CommentRepositoryCustom {
    Page<ResponseChallengeComment> searchCommentsByChallengeId(Long challengeId, Pageable pageable);
}
