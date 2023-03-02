package com.example.dailychallenge.controller;

import com.example.dailychallenge.dto.ChallengeDto;
import com.example.dailychallenge.dto.ChallengeSearchCondition;
import com.example.dailychallenge.dto.HashtagDto;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.ChallengeCategory;
import com.example.dailychallenge.entity.challenge.ChallengeDuration;
import com.example.dailychallenge.entity.challenge.ChallengeLocation;
import com.example.dailychallenge.entity.challenge.UserChallenge;
import com.example.dailychallenge.entity.hashtag.ChallengeHashtag;
import com.example.dailychallenge.entity.hashtag.Hashtag;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.exception.users.UserNotFound;
import com.example.dailychallenge.service.challenge.ChallengeService;
import com.example.dailychallenge.service.challenge.UserChallengeService;
import com.example.dailychallenge.service.hashtag.ChallengeHashtagService;
import com.example.dailychallenge.service.hashtag.HashtagService;
import com.example.dailychallenge.service.users.UserService;
import com.example.dailychallenge.vo.challenge.RequestChallengeQuestion;
import com.example.dailychallenge.vo.challenge.RequestCreateChallenge;
import com.example.dailychallenge.vo.challenge.RequestUpdateChallenge;
import com.example.dailychallenge.vo.challenge.ResponseChallenge;
import com.example.dailychallenge.vo.challenge.ResponseChallengeWithParticipatedUsersInfo;
import com.example.dailychallenge.vo.challenge.ResponseCreateChallenge;
import com.example.dailychallenge.vo.challenge.ResponseRecommendedChallenge;
import com.example.dailychallenge.vo.challenge.ResponseUpdateChallenge;
import com.example.dailychallenge.vo.challenge.ResponseUserChallenge;
import java.util.List;
import java.util.stream.Collectors;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
            @RequestPart(required = false) List<MultipartFile> challengeImgFiles,
            @RequestPart(value = "hashtagDto", required = false) HashtagDto hashtagDto) {
        ModelMapper mapper = new ModelMapper();
        String userEmail = user.getUsername();
        User findUser = userService.findByEmail(userEmail).orElseThrow(UserNotFound::new);
        ChallengeDto challengeDto = mapper.map(requestCreateChallenge, ChallengeDto.class);

        Challenge challenge = challengeService.saveChallenge(challengeDto, challengeImgFiles, findUser);
        UserChallenge userChallenge = userChallengeService.saveUserChallenge(challenge, findUser);
        userChallenge.challengeParticipate();

        if (hashtagDto != null) {
            List<String> hashtagContents = hashtagDto.getContent();
            List<Hashtag> hashtags = hashtagService.saveHashtag(hashtagContents);
            challengeHashtagService.saveChallengeHashtag(challenge, hashtags);
        }

        ResponseCreateChallenge responseCreateChallenge = ResponseCreateChallenge.create(challenge, userChallenge);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseCreateChallenge);
    }

    @GetMapping("/challenge/{challengeId}")
    public ResponseEntity<ResponseChallengeWithParticipatedUsersInfo> findChallengeById(
            @PathVariable Long challengeId) {
        ResponseChallenge responseChallenge = challengeService.searchById(challengeId);
        List<ResponseUserChallenge> responseUserChallenges = userChallengeService.searchByChallengeId(challengeId);

        ResponseChallengeWithParticipatedUsersInfo responseChallengeWithParticipatedUsersInfo =
                ResponseChallengeWithParticipatedUsersInfo.builder()
                        .responseChallenge(responseChallenge)
                        .responseUserChallenges(responseUserChallenges)
                        .build();
        return ResponseEntity.status(HttpStatus.OK).body(responseChallengeWithParticipatedUsersInfo);
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

    @GetMapping("/challenge/question")
    public ResponseEntity<List<ResponseRecommendedChallenge>> searchChallengesByQuestion(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user,
            @RequestBody RequestChallengeQuestion requestChallengeQuestion) {

        ChallengeCategory challengeCategory = ChallengeCategory.findByIndex(
                requestChallengeQuestion.getChallengeCategoryIndex());
        ChallengeDuration challengeDuration = ChallengeDuration.findByIndex(
                requestChallengeQuestion.getChallengeDurationIndex());
        ChallengeLocation challengeLocation = ChallengeLocation.findByIndex(
                requestChallengeQuestion.getChallengeLocationIndex());

        List<ResponseRecommendedChallenge> recommendedChallenges = challengeService.searchByQuestion(
                challengeCategory, challengeDuration, challengeLocation);

        return ResponseEntity.status(HttpStatus.OK).body(recommendedChallenges);
    }

    @PostMapping("/challenge/{challengeId}")
    public ResponseEntity<ResponseUpdateChallenge> updateChallenge(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user,
            @PathVariable Long challengeId,
            @RequestPart @Valid RequestUpdateChallenge requestUpdateChallenge,
            @RequestPart(required = false) List<MultipartFile> updateChallengeImgFiles,
            @RequestPart(value = "hashtagDto", required = false) HashtagDto hashtagDto) {

        String userEmail = user.getUsername();
        User findUser = userService.findByEmail(userEmail).orElseThrow(UserNotFound::new);

        Challenge updatedChallenge = challengeService.updateChallenge(challengeId, requestUpdateChallenge,
                updateChallengeImgFiles, findUser);

        if (hashtagDto != null) {
            List<String> hashtagContents = hashtagDto.getContent();
            List<Hashtag> hashtags = hashtagService.updateHashtag(hashtagContents, challengeId);
            challengeHashtagService.updateChallengeHashtag(challengeId,hashtags);
        }

        ResponseUpdateChallenge responseChallenge = ResponseUpdateChallenge.create(updatedChallenge);

        return ResponseEntity.status(HttpStatus.OK).body(responseChallenge);
    }

    @DeleteMapping("/challenge/{challengeId}")
    public ResponseEntity<Void> deleteChallenge(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user,
            @PathVariable Long challengeId) {
        String userEmail = user.getUsername();
        User findUser = userService.findByEmail(userEmail).orElseThrow(UserNotFound::new);

        List<ChallengeHashtag> challengeHashtags = challengeHashtagService.findByChallengeId(challengeId);
        List<Hashtag> savedTag = challengeHashtags.stream()
                .map(ChallengeHashtag::getHashtag).collect(Collectors.toList());

        hashtagService.deleteHashtag(savedTag);
        challengeService.deleteChallenge(challengeId, findUser);

        return ResponseEntity.noContent().build();
    }
}