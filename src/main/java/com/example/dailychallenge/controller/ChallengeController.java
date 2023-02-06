package com.example.dailychallenge.controller;

import com.example.dailychallenge.dto.ChallengeDto;
import com.example.dailychallenge.dto.ChallengeSearchCondition;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.UserChallenge;
import com.example.dailychallenge.entity.hashtag.Hashtag;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.exception.users.UserNotFound;
import com.example.dailychallenge.service.challenge.ChallengeService;
import com.example.dailychallenge.service.challenge.UserChallengeService;
import com.example.dailychallenge.service.hashtag.ChallengeHashtagService;
import com.example.dailychallenge.service.hashtag.HashtagService;
import com.example.dailychallenge.service.users.UserService;
import com.example.dailychallenge.vo.RequestCreateChallenge;
import com.example.dailychallenge.vo.ResponseChallenge;
import com.example.dailychallenge.vo.ResponseCreateChallenge;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ChallengeController {

    private final ChallengeService challengeService;
    private final UserChallengeService userChallengeService;
    private final HashtagService hashtagService;
    private final ChallengeHashtagService challengeHashtagService;
    private final UserService userService;


    @PostMapping(value = "/challenge/new", consumes = {MediaType.APPLICATION_JSON_VALUE,
            MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<ResponseCreateChallenge> createChallenge(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user,
            @RequestPart @Valid RequestCreateChallenge requestCreateChallenge,
            @RequestPart(required = false) MultipartFile challengeImgFile,
            @RequestPart(value = "hashtagDto", required = false) List<String> hashtagDto) {
        ModelMapper mapper = new ModelMapper();
        String userEmail = user.getUsername();
        User findUser = userService.findByEmail(userEmail);
        if (findUser == null) {
            throw new UserNotFound();
        }
        ChallengeDto challengeDto = mapper.map(requestCreateChallenge, ChallengeDto.class);

        Challenge challenge = challengeService.saveChallenge(challengeDto, challengeImgFile, findUser);
        UserChallenge userChallenge = userChallengeService.saveUserChallenge(challenge, findUser);

        ResponseCreateChallenge responseCreateChallenge = ResponseCreateChallenge.create(challenge, userChallenge);

        if (hashtagDto != null) {
            List<Hashtag> hashtags = hashtagService.saveHashtag(hashtagDto);
            challengeHashtagService.saveChallengeHashtag(challenge, hashtags);
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(responseCreateChallenge);
    }

    @GetMapping("/challenge")
    public ResponseEntity<Page<ResponseChallenge>> searchAllChallengesSortByPopular(
            @PageableDefault(page = 0, size = 10, sort = "popular", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ResponseChallenge> responseChallenges = userChallengeService.searchAll(pageable);

        return ResponseEntity.status(HttpStatus.OK).body(responseChallenges);
    }

    @GetMapping("/challenge/condition")
    public ResponseEntity<Page<ResponseChallenge>> searchChallengesByConditionSortByPopular(
            ChallengeSearchCondition condition,
            @PageableDefault(page = 0, size = 10, sort = "popular", direction = Sort.Direction.DESC) Pageable pageable) {

        Page<ResponseChallenge> responseChallenges = userChallengeService.searchByCondition(condition, pageable);

        return ResponseEntity.status(HttpStatus.OK).body(responseChallenges);
    }
}