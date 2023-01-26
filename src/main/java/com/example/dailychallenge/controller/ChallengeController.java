package com.example.dailychallenge.controller;

import com.example.dailychallenge.dto.ChallengeDto;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.UserChallenge;
import com.example.dailychallenge.service.challenge.ChallengeService;
import com.example.dailychallenge.service.challenge.UserChallengeService;
import com.example.dailychallenge.vo.RequestCreateChallenge;
import com.example.dailychallenge.vo.ResponseCreateChallenge;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;
    private final UserChallengeService userChallengeService;

    @PostMapping("/challenge/new")
    public ResponseEntity<ResponseCreateChallenge> createChallenge(@AuthenticationPrincipal org.springframework.security.core.userdetails.User user, @RequestBody @Valid RequestCreateChallenge requestCreateChallenge) throws Exception {
        ModelMapper mapper = new ModelMapper();
        String userEmail = user.getUsername();
        ChallengeDto challengeDto = mapper.map(requestCreateChallenge, ChallengeDto.class);

        Challenge challenge = challengeService.saveChallenge(challengeDto);
        UserChallenge userChallenge = userChallengeService.saveUserChallenge(challenge, userEmail);

        ResponseCreateChallenge responseCreateChallenge = ResponseCreateChallenge.create(challenge, userChallenge);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseCreateChallenge);
    }
}