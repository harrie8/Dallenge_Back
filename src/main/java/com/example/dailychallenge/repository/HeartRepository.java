package com.example.dailychallenge.repository;

import com.example.dailychallenge.entity.Heart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface HeartRepository extends JpaRepository<Heart,Long> {
    Optional<Heart> findByUsers_IdAndComment_Id(@Param(value = "userId")Long userId, @Param(value = "commentId") Long commentId);
    Long countByCommentId(Long commentId);
}
