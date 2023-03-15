package com.example.dailychallenge.repository.badge;

import com.example.dailychallenge.entity.badge.UserBadge;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Long> {
    List<UserBadge> findAllByUsersId(Long userId);

    Optional<UserBadge> findByUsersIdAndBadgeName(Long userId, String badgeName);
}
