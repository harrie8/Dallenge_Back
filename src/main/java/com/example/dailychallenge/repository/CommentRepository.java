package com.example.dailychallenge.repository;

import com.example.dailychallenge.entity.comment.Comment;
import com.example.dailychallenge.repository.comment.CommentRepositoryCustom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {
}
