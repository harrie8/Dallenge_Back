package com.example.dailychallenge.controller;

import com.example.dailychallenge.dto.BadgeDto;
import com.example.dailychallenge.dto.EmailDto;
import com.example.dailychallenge.dto.UserDto;
import com.example.dailychallenge.entity.badge.Badge;
import com.example.dailychallenge.entity.badge.type.AchievementBadgeType;
import com.example.dailychallenge.entity.badge.type.ChallengeCreateBadgeType;
import com.example.dailychallenge.entity.badge.type.CommentWriteBadgeType;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.exception.users.UserDuplicateCheck;
import com.example.dailychallenge.exception.users.UserLoginFailure;
import com.example.dailychallenge.exception.users.UserNotFound;
import com.example.dailychallenge.exception.users.UserPasswordCheck;
import com.example.dailychallenge.service.badge.BadgeService;
import com.example.dailychallenge.service.badge.UserBadgeEvaluationService;
import com.example.dailychallenge.service.badge.UserBadgeService;
import com.example.dailychallenge.service.email.EmailService;
import com.example.dailychallenge.service.users.UserService;
import com.example.dailychallenge.utils.JwtTokenUtil;
import com.example.dailychallenge.utils.RandomPasswordGenerator;
import com.example.dailychallenge.vo.RequestLogin;
import com.example.dailychallenge.vo.RequestUpdateUser;
import com.example.dailychallenge.vo.RequestUser;
import com.example.dailychallenge.vo.ResponseChallengeByUserChallenge;
import com.example.dailychallenge.vo.ResponseLoginUser;
import com.example.dailychallenge.vo.ResponseUser;
import com.example.dailychallenge.vo.ResponseUserInfo;
import com.example.dailychallenge.vo.challenge.ResponseInProgressChallenge;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final UserBadgeEvaluationService userBadgeEvaluationService;
    private final BadgeService badgeService;
    private final UserBadgeService userBadgeService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final EmailService emailService;

    @PostMapping("/user/new")
    public ResponseEntity createUser(@RequestBody @Valid RequestUser requestUser) throws Exception {
        ModelMapper mapper = new ModelMapper();

        UserDto userDto = mapper.map(requestUser, UserDto.class);
        User savedUser = userService.saveUser(userDto, passwordEncoder);
        ResponseUser responseUser = mapper.map(userDto, ResponseUser.class);
        responseUser.setUserId(savedUser.getId());

        userBadgeEvaluationService.createUserBadgeEvaluation(savedUser);
        userBadgeService.saveUserBadges(savedUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

//    private void saveUserBadges(User savedUser) {
//       List<Badge> badges = badgeService.findAll();
//        if (badges.isEmpty()) {
//            saveBadges(savedUser);
//            return;
//        }
//        userBadgeService.createUserBadges(savedUser, badges);
//    }
//
//    private void saveBadges(User savedUser) {
//        List<BadgeDto> achievementBadgeDtos = AchievementBadgeType.getBadgeDtos();
//        List<BadgeDto> commentWriteBadgeDtos = CommentWriteBadgeType.getBadgeDtos();
//        List<BadgeDto> challengeCreateBadgeDtos = ChallengeCreateBadgeType.getBadgeDtos();
//        List<Badge> achievementBadges = badgeService.createBadges(achievementBadgeDtos);
//        List<Badge> commentWriteBadges = badgeService.createBadges(commentWriteBadgeDtos);
//        List<Badge> challengeCreateBadges = badgeService.createBadges(challengeCreateBadgeDtos);
//        userBadgeService.createUserBadges(savedUser, achievementBadges);
//        userBadgeService.createUserBadges(savedUser, commentWriteBadges);
//        userBadgeService.createUserBadges(savedUser, challengeCreateBadges);
//    }

    @PostMapping("/user/login")
    public ResponseEntity<?> loginUser(@RequestBody @Valid RequestLogin requestLogin) {
        ModelMapper mapper = new ModelMapper();
        try {
            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    requestLogin.getEmail(), requestLogin.getPassword()
            ));
            if (auth.isAuthenticated()) {
                UserDetails userDetails = userService.loadUserByUsername(requestLogin.getEmail());
                String token = jwtTokenUtil.generateToken(userDetails.getUsername());
                User user = userService.findByEmail(requestLogin.getEmail()).orElseThrow(UserNotFound::new);

                ResponseLoginUser responseLoginUser = mapper.map(user, ResponseLoginUser.class);
                responseLoginUser.setUserId(user.getId());
                responseLoginUser.setToken(token);

                return ResponseEntity.status(HttpStatus.OK).body(responseLoginUser);
            } else {
                /** 로그인 되지 않은 사용자인 경우  -> 소셜 로그인 이후 변경 예정 **/
                return ResponseEntity.status(401).body("Invalid Credentials");
            }
        } catch (BadCredentialsException e) { // 아이디, 비밀번호 틀린 경우
            throw new UserLoginFailure();
        }
    }

    @PostMapping("/user/{userId}")
    public void updateUser(@AuthenticationPrincipal org.springframework.security.core.userdetails.User user,
                           @PathVariable Long userId,
                           @RequestPart @Valid RequestUpdateUser requestUpdateUser,
                           @RequestPart(value = "userImgFile", required = false) MultipartFile multipartFile) {

        String loginUserEmail = user.getUsername();
        User loginUser = userService.getValidateUser(loginUserEmail, userId);

        userService.updateUser(loginUser, requestUpdateUser, multipartFile);
    }

    @DeleteMapping("/user/{userId}")
    public void deleteUser(@AuthenticationPrincipal org.springframework.security.core.userdetails.User user,
                           @PathVariable Long userId) {

        String loginUserEmail = user.getUsername();
        User loginUser = userService.getValidateUser(loginUserEmail, userId);

        userService.delete(loginUser);
    }

    @PostMapping("/user/check") // 아이디 중복 체크
    public ResponseEntity<String> checkDuplicateUser(@RequestParam String email){
        userService.findByEmail(email).ifPresent(findUser -> {
            throw new UserDuplicateCheck();
        });
        return ResponseEntity.status(HttpStatus.OK).body("사용 가능한 아이디입니다.");
    }

    @PostMapping("/user/{userId}/check") // 비밀번호 검증 url
    public ResponseEntity<String> checkUserPassword(@PathVariable("userId") Long userId,
                                                    @RequestParam String password) {
        if (!userService.checkPassword(userId, password, passwordEncoder)) {
            throw new UserPasswordCheck();
        }
        return ResponseEntity.status(HttpStatus.OK).body("비밀번호 확인이 완료되었습니다.");
    }

    @PostMapping("/user/{userId}/change") // 비밀번호 변경 url
    public ResponseEntity<?> changeUserPassword(@PathVariable("userId") Long userId,
                                               @RequestParam String oldPassword,
                                               @RequestParam String newPassword) {
        if (!userService.checkPassword(userId, oldPassword, passwordEncoder)) {
            throw new UserPasswordCheck();
        }
        userService.changePassword(userId,newPassword,passwordEncoder);
        return ResponseEntity.status(HttpStatus.OK).body("비밀번호가 변경되었습니다.");
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<ResponseUserInfo> getUserInfo(@PathVariable("userId") Long userId){
        ResponseUserInfo userInfo = userService.getUserInfo(userId);
        return ResponseEntity.status(HttpStatus.OK).body(userInfo);
    }

    @GetMapping("/user/challenge") // 내가 작성한 챌린지 조회
    public ResponseEntity<List<ResponseChallengeByUserChallenge>> getChallengeByUser(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user){
        User getUser = userService.findByEmail(user.getUsername()).orElseThrow(UserNotFound::new);
        List<ResponseChallengeByUserChallenge> userChallenge = userService.getChallengeByUser(getUser.getId());
        return ResponseEntity.status(HttpStatus.OK).body(userChallenge);
    }

    @GetMapping("/user/participate") // 내가 작성한 챌린지 + 내가 참여한 챌린지 조회
    public ResponseEntity<List<ResponseChallengeByUserChallenge>> getParticipateChallenge(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user){

        User getUser = userService.findByEmail(user.getUsername()).orElseThrow(UserNotFound::new);
        List<ResponseChallengeByUserChallenge> participateChallenges = userService.getParticipateChallenge(getUser.getId());

        List<ResponseChallengeByUserChallenge> challengesCreatedByMe = userService.getChallengeByUser(getUser.getId());

        List<ResponseChallengeByUserChallenge> concat = new ArrayList<>();
        concat.addAll(participateChallenges);
        concat.addAll(challengesCreatedByMe);

        Set<ResponseChallengeByUserChallenge> set = new HashSet<>(concat);
        List<ResponseChallengeByUserChallenge> result = new ArrayList<>(set);
        result.sort(Comparator.comparing(ResponseChallengeByUserChallenge::getCreatedAt));

        return ResponseEntity.status(HttpStatus.OK).body(result);
    }

    @GetMapping("/user/inProgress") // 내가 진행중인 챌린지 조회
    public ResponseEntity<List<ResponseInProgressChallenge>> getInProgressChallenges(
            @AuthenticationPrincipal org.springframework.security.core.userdetails.User user){

        User getUser = userService.findByEmail(user.getUsername()).orElseThrow(UserNotFound::new);
        List<ResponseInProgressChallenge> inProgressChallenges = userService.getInProgressChallenges(getUser.getId());

        return ResponseEntity.status(HttpStatus.OK).body(inProgressChallenges);
    }

    @PostMapping("/user/resetPassword") // 비밀번호 초기화 url
    public ResponseEntity<String> resetUserPassword(@RequestParam String email) {
        User user = userService.findByEmail(email).orElseThrow(UserNotFound::new);
        Long userId = user.getId();
        String randomPassword = RandomPasswordGenerator.generate(10);

        userService.changePassword(userId, randomPassword, passwordEncoder);

        EmailDto emailDto = EmailDto.builder()
                .to(user.getEmail())
                .subject(user.getUserName())
                .message(randomPassword)
                .build();
        emailService.sendMail(emailDto);

        return ResponseEntity.status(HttpStatus.OK).body("비밀번호가 임의의 문자열로 초기화되었습니다.");
    }

    /**
     * 2023-01-29
     * aws 구글 로그인 url : http://ec2-52-78-166-208.ap-northeast-2.compute.amazonaws.com:8080/oauth2/authorization/google
     * local 구글 로그인 url : http://localhost:8080/oauth2/authorization/google
     */
    @GetMapping("/api/user") // TEST
    public Authentication user(Authentication authentication, @AuthenticationPrincipal OAuth2User oAuth2User) {
        System.out.println("authentication = " + authentication + ", oAuth2User = " + oAuth2User);
        return authentication;
    }

}
