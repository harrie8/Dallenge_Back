package com.example.dailychallenge.service.badge;

import com.example.dailychallenge.entity.badge.Badge;
import com.example.dailychallenge.repository.badge.BadgeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BadgeService {
    private final BadgeRepository badgeRepository;

    @Transactional
    public Badge createBadge(String badgeName) {
        Badge badge = Badge.builder()
                .name(badgeName)
                .build();

        return badgeRepository.save(badge);
    }

    @Transactional
    public void removeBadge(Badge badge) {
        badgeRepository.delete(badge);
    }
}
