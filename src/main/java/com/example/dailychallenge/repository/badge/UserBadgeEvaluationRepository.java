package com.example.dailychallenge.repository.badge;

import com.example.dailychallenge.entity.badge.UserBadgeEvaluation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBadgeEvaluationRepository extends JpaRepository<UserBadgeEvaluation, Long> {
}
