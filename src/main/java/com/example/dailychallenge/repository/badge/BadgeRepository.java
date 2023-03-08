package com.example.dailychallenge.repository.badge;

import com.example.dailychallenge.entity.badge.Badge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BadgeRepository extends JpaRepository<Badge, Long> {
}
