package com.example.dailychallenge.controller;

import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.UserChallenge;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.exception.users.UserNotFound;
import com.example.dailychallenge.service.challenge.ChallengeService;
import com.example.dailychallenge.service.challenge.UserChallengeService;
import com.example.dailychallenge.service.users.UserService;
import com.example.dailychallenge.vo.ResponseMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserChallengeController {

    private final ChallengeService challengeService;
    private final UserChallengeService userChallengeService;
    private final UserService userService;

    @PostMapping("/challenge/{challengeId}/participate")
    public ResponseEntity<ResponseMessage> participateInChallenge(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user,
            @PathVariable Long challengeId) {

        String userEmail = user.getUsername();
        User findUser = userService.findByEmail(userEmail);
        if (findUser == null) {
            throw new UserNotFound();
        }
        Challenge findChallenge = challengeService.findById(challengeId);

        UserChallenge savedUserChallenge = userChallengeService.saveUserChallenge(findChallenge, findUser);
        userChallengeService.challengeParticipate(savedUserChallenge);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .code(201)
                .message("챌린지 참가 완료!")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage);
    }
}