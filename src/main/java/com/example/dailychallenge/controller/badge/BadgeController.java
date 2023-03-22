package com.example.dailychallenge.controller.badge;

import com.example.dailychallenge.entity.badge.Badge;
import com.example.dailychallenge.service.badge.BadgeService;
import com.example.dailychallenge.vo.badge.ResponseBadge;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
public class BadgeController {
    private final BadgeService badgeService;

    @GetMapping("/badges")
    public ResponseEntity<List<ResponseBadge>> getAllBadges() {
        List<Badge> badges = badgeService.getAll();

        List<ResponseBadge> responseBadges = ResponseBadge.create(badges);

        return ResponseEntity.status(HttpStatus.OK).body(responseBadges);
    }
}