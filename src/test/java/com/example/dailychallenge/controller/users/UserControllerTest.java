package com.example.dailychallenge.controller.users;

import static com.example.dailychallenge.util.fixture.TokenFixture.AUTHORIZATION;
import static com.example.dailychallenge.util.fixture.TokenFixture.EMAIL;
import static com.example.dailychallenge.util.fixture.TokenFixture.PASSWORD;
import static com.example.dailychallenge.util.fixture.TokenFixture.TOKEN_PREFIX;
import static com.example.dailychallenge.util.fixture.challenge.ChallengeFixture.createChallengeDto;
import static com.example.dailychallenge.util.fixture.user.UserFixture.USERNAME;
import static com.example.dailychallenge.util.fixture.user.UserFixture.createOtherUser;
import static com.example.dailychallenge.util.fixture.user.UserFixture.getRequestPostProcessor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.dailychallenge.dto.ChallengeDto;
import com.example.dailychallenge.dto.UserDto;
import com.example.dailychallenge.entity.badge.Badge;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.ChallengeCategory;
import com.example.dailychallenge.entity.challenge.ChallengeDuration;
import com.example.dailychallenge.entity.challenge.ChallengeLocation;
import com.example.dailychallenge.entity.challenge.UserChallenge;
import com.example.dailychallenge.entity.comment.Comment;
import com.example.dailychallenge.entity.comment.CommentImg;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.exception.users.UserDuplicateCheck;
import com.example.dailychallenge.exception.users.UserDuplicateNotCheck;
import com.example.dailychallenge.exception.users.UserPasswordCheck;
import com.example.dailychallenge.repository.badge.BadgeRepository;
import com.example.dailychallenge.repository.badge.UserBadgeRepository;
import com.example.dailychallenge.service.challenge.ChallengeService;
import com.example.dailychallenge.service.users.UserService;
import com.example.dailychallenge.util.fixture.TestDataSetup;
import com.example.dailychallenge.utils.JwtTokenUtil;
import com.example.dailychallenge.vo.RequestChangePassword;
import com.example.dailychallenge.vo.RequestUpdateUser;
import com.example.dailychallenge.vo.RequestUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
@Import({TestDataSetup.class})
class UserControllerTest {
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserService userService;
    @Autowired
    private ChallengeService challengeService;
    @Autowired
    private BadgeRepository badgeRepository;
    @Autowired
    private UserBadgeRepository userBadgeRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private TestDataSetup testDataSetup;

    public UserDto createUser() {
        UserDto userDto = new UserDto();
        userDto.setEmail("test1234@test.com");
        userDto.setUserName("홍길동");
        userDto.setInfo("testInfo");
        userDto.setPassword("1234");
        return userDto;
    }

    MockMultipartFile createMultipartFiles() {
        String path = "userImgFile";
        String imageName = "editImage.jpg";
        MockMultipartFile multipartFile = new MockMultipartFile(path, imageName,
                "image/jpg", new byte[]{1, 2, 3, 4});
        return multipartFile;
    }

    public Challenge createChallenge() throws Exception {
        User savedUser = userService.saveUser(createUser(), passwordEncoder);
        ChallengeDto challengeDto = ChallengeDto.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .challengeCategory(ChallengeCategory.STUDY.getDescription())
                .challengeLocation(ChallengeLocation.INDOOR.getDescription())
                .challengeDuration(ChallengeDuration.WITHIN_TEN_MINUTES.getDescription())
                .build();
        MultipartFile challengeImg = createMultipartFiles();
        List<MultipartFile> challengeImgFiles = List.of(challengeImg);
        return challengeService.saveChallenge(challengeDto, challengeImgFiles, savedUser);
    }
    public Challenge createChallenge2() {
        UserDto userDto = new UserDto();
        userDto.setEmail("test12345@test.com");
        userDto.setUserName("홍길동");
        userDto.setInfo("testInfo");
        userDto.setPassword("1234");

        User savedUser = userService.saveUser(userDto, passwordEncoder);
        ChallengeDto challengeDto = ChallengeDto.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .challengeCategory(ChallengeCategory.STUDY.getDescription())
                .challengeLocation(ChallengeLocation.INDOOR.getDescription())
                .challengeDuration(ChallengeDuration.WITHIN_TEN_MINUTES.getDescription())
                .build();
        MultipartFile challengeImg = createMultipartFiles();
        List<MultipartFile> challengeImgFiles = List.of(challengeImg);
        return challengeService.saveChallenge(challengeDto, challengeImgFiles, savedUser);
    }

    @Test
    @DisplayName("회원가입 테스트")
    public void newUserTest() throws Exception {
        RequestUser requestUser = RequestUser.builder()
                .userName("GilDong")
                .email("test@test.com")
                .password("1234")
                .build();

        mockMvc.perform(post("/user/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUser))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());
    }

    @Test
    @DisplayName("로그인 테스트")
    public void loginUserTest() throws Exception {
        Map<String, String> loginData = new HashMap<>();
        loginData.put("email", "test1234@test.com");
        loginData.put("password", "1234");

        userService.saveUser(createUser(), passwordEncoder);

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginData))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("회원 정보 수정 api 테스트")
    public void updateUserTest() throws Exception {
        User savedUser = userService.saveUser(createUser(), passwordEncoder);
        Long userId = savedUser.getId();
        RequestUpdateUser requestUpdateUserBuild = RequestUpdateUser.builder()
                .userName("editName")
                .info("editInfo")
                .build();

        MockMultipartFile userImgFile = createMultipartFiles();
        String data = objectMapper.writeValueAsString(requestUpdateUserBuild);
        MockMultipartFile requestUpdateUser = new MockMultipartFile("requestUpdateUser", "requestUpdateUser",
                "application/json", data.getBytes(
                StandardCharsets.UTF_8));

        String token = generateToken();
        mockMvc.perform(multipart("/user/{userId}", userId)
                        .file(userImgFile)
                        .file(requestUpdateUser)
                        .header(AUTHORIZATION, token)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("회원 삭제 api 테스트")
    public void deleteUserTest() throws Exception {
        User savedUser = userService.saveUser(createUser(), passwordEncoder);
        Long userId = savedUser.getId();

        String token = generateToken();
        mockMvc.perform(delete("/user/{userId}", userId)
                        .header(AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("회원 아이디 중복 에러 테스트 - 검증 url")
    public void duplicateUserIdTest() throws Exception {
        User user = userService.saveUser(createUser(), passwordEncoder);
        String token = generateToken();

        mockMvc.perform(post("/user/check")
                        .param("email","test1234@test.com")
                        .header(AUTHORIZATION, token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(result ->
                        assertTrue(result.getResolvedException().getClass().isAssignableFrom(UserDuplicateCheck.class)))
                .andDo(print());
    }

    @Test // 중복일 때만 예외 발생
    @DisplayName("회원가입 중복 에러 테스트 - 아이디 중복 체크를 안하고 회원가입하는 경우")
    public void duplicateUserTest() throws Exception {
        userService.saveUser(createUser(), passwordEncoder);
        RequestUser requestUser = RequestUser.builder()
                .userName("GilDong")
                .email("test1234@test.com")
                .password("1234")
                .build();

        mockMvc.perform(post("/user/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUser))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(result ->
                        assertTrue(result.getResolvedException().getClass().isAssignableFrom(UserDuplicateNotCheck.class)))
                .andDo(print());
    }

    @Test
    @DisplayName("비밀번호 검증 테스트")
    public void checkUserPassword() throws Exception {
        User user = userService.saveUser(createUser(), passwordEncoder);
        mockMvc.perform(post("/user/{userId}/check",user.getId())
                        .header(AUTHORIZATION, generateToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("password","1234")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("비밀번호 검증 에러 테스트")
    public void checkUserPasswordError() throws Exception {
        User user = userService.saveUser(createUser(), passwordEncoder);

        mockMvc.perform(post("/user/{userId}/check",user.getId())
                        .header(AUTHORIZATION, generateToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("password","12345")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().is4xxClientError())
                .andExpect(result ->
                        assertTrue(result.getResolvedException().getClass().isAssignableFrom(UserPasswordCheck.class)))
                .andDo(print());
    }

    @Test
    @DisplayName("비밀번호 변경 테스트")
    public void changeUserPassword() throws Exception {
        User user = userService.saveUser(createUser(), passwordEncoder);

        RequestChangePassword requestChangePassword = RequestChangePassword.builder()
                .oldPassword("1234")
                .newPassword("12345")
                .build();
        String json = objectMapper.writeValueAsString(requestChangePassword);

        mockMvc.perform(post("/user/{userId}/change",user.getId())
                        .header(AUTHORIZATION, generateToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());

        boolean isPasswordChanged = passwordEncoder.matches(requestChangePassword.getNewPassword(), user.getPassword());
        assertTrue(isPasswordChanged);
    }

    @Test
    @DisplayName("회원정보 조회 테스트")
    public void getUserInfo() throws Exception {
        User user = userService.saveUser(createUser(), passwordEncoder);
        String token = generateToken();
        mockMvc.perform(get("/user/{userId}", user.getId())
                        .header(AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @Test
    @DisplayName("내가 작성한 챌린지 조회 테스트")
    public void getUserChallenge() throws Exception {
        createChallenge();
        String token = generateToken();

        mockMvc.perform(get("/user/challenge")
                        .header(AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }
    @Test
    @DisplayName("내가 참가한 챌린지 조회 테스트")
    public void getParticipateChallenge() throws Exception {
        Challenge challenge = createChallenge2();

        User savedUser = testDataSetup.saveUser(USERNAME, EMAIL, PASSWORD);

        UserChallenge userChallenge = testDataSetup.챌린지에_참가한다(challenge, savedUser);
        Comment comment = testDataSetup.챌린지에_댓글을_단다(challenge, savedUser, "content");
        CommentImg commentImg1 = testDataSetup.댓글에_이미지를_추가한다(comment);
        CommentImg commentImg2 = testDataSetup.댓글에_이미지를_추가한다(comment);
        testDataSetup.챌린지에_댓글을_단다(challenge, savedUser, null);

        mockMvc.perform(get("/user/participate")
                        .with(getRequestPostProcessor(savedUser))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(savedUser.getId()))
                .andExpect(jsonPath("$[0].challengeId").value(challenge.getId()))
                .andExpect(jsonPath("$[0].challengeTitle").value(challenge.getTitle()))
                .andExpect(jsonPath("$[0].challengeContent").value(challenge.getContent()))
                .andExpect(jsonPath("$[0].challengeStatus").value(userChallenge.getChallengeStatus().toString()))
                .andExpect(jsonPath("$[0].createdAt").isNotEmpty())
                .andExpect(jsonPath("$[0].comments.[0].commentId").isNotEmpty())
                .andExpect(jsonPath("$[0].comments.[0].commentContent").value(comment.getContent()))
                .andExpect(jsonPath("$[0].comments.[0].commentImgs",
                        contains(commentImg1.getImgUrl(), commentImg2.getImgUrl())))
                .andExpect(jsonPath("$[0].comments.[0].commentCreatedAt").value(comment.getMonthDayFormatCreatedAt()))
                .andDo(print());
    }

    @Test
    @DisplayName("회원가입하면 뱃지 생성되는지 테스트")
    void createBadgesWhenNewUserTest() throws Exception {
        RequestUser requestUser = RequestUser.builder()
                .userName("GilDong")
                .email("test@test.com")
                .password("1234")
                .build();

        mockMvc.perform(post("/user/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestUser))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());

        List<Badge> badges = badgeRepository.findAll();
        assertEquals(15, badges.size());
        assertThat(badges).extracting("imgUrl").isNotEmpty();
        assertTrue(userBadgeRepository.findAll().stream().allMatch(userBadge -> userBadge.getStatus().equals(false)));
    }

    @Test
    @DisplayName("내가 진행중인 챌린지들 조회 테스트")
    public void getInProcessChallenges() throws Exception {
        User user = userService.saveUser(createUser(), passwordEncoder);
        User otherUser = userService.saveUser(createOtherUser(), passwordEncoder);

        List<Challenge> challenges = new ArrayList<>();
        List<UserChallenge> userChallenges = new ArrayList<>();
        List<Comment> comments = new ArrayList<>();
        List<CommentImg> commentImgs = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Challenge otherUserChallenge = testDataSetup.챌린지를_생성한다(createChallengeDto(), otherUser);
            testDataSetup.챌린지에_참가한다(otherUserChallenge, otherUser);
            UserChallenge userChallenge = testDataSetup.챌린지에_참가한다(otherUserChallenge, user);
            Comment comment = testDataSetup.챌린지에_댓글을_단다(otherUserChallenge, user);
            CommentImg commentImg = testDataSetup.댓글에_이미지를_추가한다(comment);

            challenges.add(otherUserChallenge);
            userChallenges.add(userChallenge);
            commentImgs.add(commentImg);
            comments.add(comment);
        }

        mockMvc.perform(get("/user/inProgress")
                        .with(getRequestPostProcessor(user))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].userId").value(user.getId()))
                .andExpect(jsonPath("$[0].challengeId").value(challenges.get(0).getId()))
                .andExpect(jsonPath("$[0].challengeTitle").value(challenges.get(0).getTitle()))
                .andExpect(jsonPath("$[0].challengeContent").value(challenges.get(0).getContent()))
                .andExpect(jsonPath("$[0].challengeStatus").value(userChallenges.get(0).getChallengeStatus().toString()))
                .andExpect(jsonPath("$[0].createdAt").isNotEmpty())
                .andExpect(jsonPath("$[0].comments[0].commentId").value(comments.get(0).getId()))
                .andExpect(jsonPath("$[0].comments[0].commentContent").value(comments.get(0).getContent()))
                .andExpect(jsonPath("$[0].comments[0].commentImgs").value(comments.get(0).getImgUrls()))
                .andExpect(jsonPath("$[0].comments[0].commentCreatedAt").value(comments.get(0).getMonthDayFormatCreatedAt()))
                .andExpect(jsonPath("$[0].howManyDaysInProgress").value(1L))
                .andExpect(jsonPath("$[0].weeklyAchievement").isArray())
                .andDo(print());
    }

    private String generateToken() {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(EMAIL, PASSWORD));
        if (auth.isAuthenticated()) {
            UserDetails userDetails = userService.loadUserByUsername(EMAIL);
            return TOKEN_PREFIX + jwtTokenUtil.generateToken(userDetails.getUsername());
        }

        throw new IllegalArgumentException("token 생성 오류");
    }
}