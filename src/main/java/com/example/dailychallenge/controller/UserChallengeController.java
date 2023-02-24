package com.example.dailychallenge.controller;

import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.UserChallenge;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.exception.users.UserNotFound;
import com.example.dailychallenge.service.challenge.ChallengeService;
import com.example.dailychallenge.service.challenge.UserChallengeService;
import com.example.dailychallenge.service.users.UserService;
import com.example.dailychallenge.vo.ResponseChallengeByUserChallenge;
import com.example.dailychallenge.vo.ResponseMessage;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
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
        User findUser = userService.findByEmail(userEmail).orElseThrow(UserNotFound::new);
        Challenge findChallenge = challengeService.findById(challengeId);

        UserChallenge savedUserChallenge = userChallengeService.saveUserChallenge(findChallenge, findUser);
        userChallengeService.challengeParticipate(savedUserChallenge);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .code(201)
                .message("챌린지 참가 완료!")
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(responseMessage);
    }

    @DeleteMapping("/challenge/{challengeId}/leave")
    public ResponseEntity<ResponseMessage> leaveChallenge(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user,
            @PathVariable Long challengeId) {

        String userEmail = user.getUsername();
        User findUser = userService.findByEmail(userEmail).orElseThrow(UserNotFound::new);
        Challenge findChallenge = challengeService.findById(challengeId);

        userChallengeService.challengeLeave(findChallenge.getId(), findUser.getId());

        ResponseMessage responseMessage = ResponseMessage.builder()
                .code(200)
                .message("챌린지 나가기 완료!")
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }


    @PostMapping("/challenge/{challengeId}/success")
    public ResponseEntity<ResponseMessage> succeedInChallenge(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user,
            @PathVariable Long challengeId){
        String userEmail = user.getUsername();
        User findUser = userService.findByEmail(userEmail).orElseThrow(UserNotFound::new);
        userChallengeService.succeedInChallenge(findUser.getId(), challengeId);

        ResponseMessage responseMessage = ResponseMessage.builder()
                .code(200)
                .message("챌린지 달성 완료!")
                .build();

        return ResponseEntity.status(HttpStatus.OK).body(responseMessage);
    }

    @GetMapping("/user/done") // 오늘 수행한 챌린지
    public ResponseEntity<List<ResponseChallengeByUserChallenge>> getTodayUserChallenge(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user
    ){
        User findUser = userService.findByEmail(user.getUsername()).orElseThrow(UserNotFound::new);
        List<ResponseChallengeByUserChallenge> userChallenge
                = userChallengeService.getTodayUserChallenge(findUser.getId());

        return ResponseEntity.status(HttpStatus.OK).body(userChallenge);
    }
}