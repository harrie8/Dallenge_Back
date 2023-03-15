package com.example.dailychallenge.service.badge;

import com.example.dailychallenge.entity.badge.Badge;
import com.example.dailychallenge.entity.badge.UserBadge;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.exception.badge.UserBadgeNotFound;
import com.example.dailychallenge.repository.badge.UserBadgeRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserBadgeService {
    private final UserBadgeRepository userBadgeRepository;

    @Transactional
    public List<UserBadge> createUserBadges(User user, List<Badge> badges) {
        List<UserBadge> userBadges = badges.stream()
                .map(badge -> UserBadge.builder()
                        .status(false)
                        .users(user)
                        .badge(badge)
                        .build()
                )
                .collect(Collectors.toUnmodifiableList());

        return userBadgeRepository.saveAll(userBadges);
    }

    public List<UserBadge> findAllByUserId(Long userId) {
        return userBadgeRepository.findAllByUsersId(userId);
    }

    public UserBadge findByUsersIdAndBadgeName(Long userId, String badgeName) {
        return userBadgeRepository.findByUsersIdAndBadgeName(userId, badgeName)
                .orElseThrow(UserBadgeNotFound::new);
    }
}
