package com.example.dailychallenge.repository;

import com.example.dailychallenge.entity.comment.Comment;
import com.example.dailychallenge.repository.comment.CommentRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {
    List<Comment> findByUsers_IdAndChallenge_Id(
            @Param(value = "userId")Long userId, @Param(value = "challengeId") Long challengeId);
}
