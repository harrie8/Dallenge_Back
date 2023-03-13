package com.example.dailychallenge.controller.badge;

import com.example.dailychallenge.entity.badge.UserBadge;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.exception.users.UserNotFound;
import com.example.dailychallenge.service.badge.UserBadgeService;
import com.example.dailychallenge.service.users.UserService;
import com.example.dailychallenge.vo.badge.ResponseUserBadge;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Validated
public class UserBadgeController {
    private final UserService userService;
    private final UserBadgeService userBadgeService;

    @GetMapping("/user/badges")
    public ResponseEntity<ResponseUserBadge> getAllUserBadges(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user) {

        String userEmail = user.getUsername();
        User findUser = userService.findByEmail(userEmail).orElseThrow(UserNotFound::new);
        Long userId = findUser.getId();

        List<UserBadge> userBadges =  userBadgeService.findAllByUserId(userId);
        ResponseUserBadge responseUserBadges = ResponseUserBadge.create(userBadges);

        return ResponseEntity.status(HttpStatus.OK).body(responseUserBadges);
    }
}