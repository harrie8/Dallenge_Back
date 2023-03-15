package com.example.dailychallenge.service.badge;

import com.example.dailychallenge.entity.badge.Badge;
import com.example.dailychallenge.repository.badge.BadgeRepository;
import java.util.List;
import java.util.stream.Collectors;
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

    public List<Badge> createBadges(List<String> badgeNames) {
        List<Badge> badges = badgeNames.stream()
                .map(badgeName -> Badge.builder()
                        .name(badgeName)
                        .build())
                .collect(Collectors.toUnmodifiableList());

        return badgeRepository.saveAll(badges);
    }

    public List<Badge> findAll() {
        return badgeRepository.findAll();
    }

    @Transactional
    public void removeBadge(Badge badge) {
        badgeRepository.delete(badge);
    }
}
