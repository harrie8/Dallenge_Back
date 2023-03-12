package com.example.dailychallenge.service.badge;

import com.example.dailychallenge.entity.badge.Badge;
import com.example.dailychallenge.entity.badge.UserBadge;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.repository.badge.UserBadgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserBadgeService {
    private final UserBadgeRepository userBadgeRepository;

    @Transactional
    public UserBadge createUserBadge(User user, Badge badge) {
        UserBadge userBadge = UserBadge
                .builder()
                .users(user)
                .badge(badge)
                .build();

        return userBadgeRepository.save(userBadge);
    }

    @Transactional
    public void removeUserBadge(UserBadge userBadge) {
        userBadgeRepository.delete(userBadge);
    }
}
