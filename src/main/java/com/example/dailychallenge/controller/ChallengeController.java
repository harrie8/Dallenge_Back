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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;
    private final UserChallengeService userChallengeService;

    @PostMapping(value = "/challenge/new", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ResponseCreateChallenge> createChallenge(@AuthenticationPrincipal org.springframework.security.core.userdetails.User user,
                                                                   @RequestPart @Valid RequestCreateChallenge requestCreateChallenge,
                                                                   @RequestPart(required = false) MultipartFile challengeImgFile) throws Exception {
        ModelMapper mapper = new ModelMapper();
        String userEmail = user.getUsername();
        ChallengeDto challengeDto = mapper.map(requestCreateChallenge, ChallengeDto.class);

        Challenge challenge = challengeService.saveChallenge(challengeDto, challengeImgFile);
        UserChallenge userChallenge = userChallengeService.saveUserChallenge(challenge, userEmail);

        ResponseCreateChallenge responseCreateChallenge = ResponseCreateChallenge.create(challenge, userChallenge);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseCreateChallenge);
    }
}