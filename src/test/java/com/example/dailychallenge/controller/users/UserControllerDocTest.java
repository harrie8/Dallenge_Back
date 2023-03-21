package com.example.dailychallenge.controller.users;

import static com.example.dailychallenge.util.fixture.TokenFixture.AUTHORIZATION;
import static com.example.dailychallenge.util.fixture.TokenFixture.EMAIL;
import static com.example.dailychallenge.util.fixture.TokenFixture.PASSWORD;
import static com.example.dailychallenge.util.fixture.TokenFixture.TOKEN_PREFIX;
import static com.example.dailychallenge.util.fixture.challenge.ChallengeFixture.createChallengeDto;
import static com.example.dailychallenge.util.fixture.user.UserFixture.USERNAME;
import static com.example.dailychallenge.util.fixture.user.UserFixture.createOtherUser;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.removeHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.relaxedResponseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.dailychallenge.dto.ChallengeDto;
import com.example.dailychallenge.dto.UserDto;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.ChallengeCategory;
import com.example.dailychallenge.entity.challenge.ChallengeDuration;
import com.example.dailychallenge.entity.challenge.ChallengeLocation;
import com.example.dailychallenge.entity.challenge.UserChallenge;
import com.example.dailychallenge.entity.comment.Comment;
import com.example.dailychallenge.entity.comment.CommentImg;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.exception.users.UserNotFound;
import com.example.dailychallenge.service.challenge.ChallengeService;
import com.example.dailychallenge.service.challenge.UserChallengeService;
import com.example.dailychallenge.service.users.UserService;
import com.example.dailychallenge.util.fixture.TestDataSetup;
import com.example.dailychallenge.utils.JwtTokenUtil;
import com.example.dailychallenge.vo.RequestLogin;
import com.example.dailychallenge.vo.RequestUpdateUser;
import com.example.dailychallenge.vo.RequestUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.dailychallenge.com", uriPort = 443)
@ExtendWith(RestDocumentationExtension.class)
@TestPropertySource(locations = "classpath:application-test.properties")
@Import({TestDataSetup.class})
public class UserControllerDocTest {
    @Autowired
    private UserService userService;
    @Autowired
    private ChallengeService challengeService;
    @Autowired
    private UserChallengeService userChallengeService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PasswordEncoder passwordEncoder;
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

    public Challenge createChallenge() {
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
    @DisplayName("회원 가입")
    void registerUser() throws Exception {
        RequestUser requestUser = RequestUser.builder()
                .userName("GilDong")
                .email("test@test.com")
                .password("1234")
                .build();

        String json = objectMapper.writeValueAsString(requestUser);

        mockMvc.perform(post("/user/new")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print())
                .andDo(document("user-register",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(
                                removeHeaders("Vary", "X-Content-Type-Options", "X-XSS-Protection", "Pragma", "Expires",
                                        "Cache-Control", "Strict-Transport-Security", "X-Frame-Options"),
                                prettyPrint()),
                        requestFields(
                                fieldWithPath("userName").description("이름"),
                                fieldWithPath("email").description("이메일")
                                        .attributes(key("constraints").value("이메일 형식")),
                                fieldWithPath("password").description("비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("email").description("회원가입 성공한 email"),
                                fieldWithPath("userName").description("회원가입 성공한 username"),
                                fieldWithPath("userId").description("회원가입 성공한 userId")
                        )
                ));
    }

    @Test
    @DisplayName("로그인")
    public void loginUser() throws Exception {
        userService.saveUser(createUser(), passwordEncoder);

        RequestLogin requestLogin = RequestLogin.builder()
                .email("test1234@test.com")
                .password("1234")
                .build();

        String json = objectMapper.writeValueAsString(requestLogin);

        mockMvc.perform(post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("user-login",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(
                                removeHeaders("Vary", "X-Content-Type-Options", "X-XSS-Protection", "Cache-Control",
                                        "Pragma", "Expires", "Strict-Transport-Security", "X-Frame-Options"),
                                prettyPrint()),
                        requestFields(
                                fieldWithPath("email").description("이메일")
                                        .attributes(key("constraints").value("이메일 형식")),
                                fieldWithPath("password").description("비밀번호")
                        ),
                        responseFields(
                                fieldWithPath("token").description("인증 토큰 값"),
                                fieldWithPath("userId").description("회원 식별자"),
                                fieldWithPath("userName").description("회원 닉네임")
                        )
                ));
    }

    @Test
    @DisplayName("회원 정보 수정")
    void updateUser() throws Exception {
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
                        .file(requestUpdateUser)
                        .file(userImgFile)
                        .header(AUTHORIZATION, token)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("user-update",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(
                                removeHeaders("Vary", "X-Content-Type-Options", "X-XSS-Protection", "Pragma", "Expires",
                                        "Cache-Control", "Strict-Transport-Security", "X-Frame-Options"),
                                prettyPrint()),
                        pathParameters(
                                parameterWithName("userId").description("회원 ID")
                        ),
                        requestParts(
                                partWithName("requestUpdateUser").description("회원 정보 수정 데이터(JSON)")
                                        .attributes(key("type").value("JSON")),
                                partWithName("userImgFile").description("회원 프로필 이미지(FILE)").optional()
                                        .attributes(key("type").value(".jpg"))
                        ),
                        requestPartFields("requestUpdateUser",
                                fieldWithPath("userName").description("회원 이름"),
                                fieldWithPath("info").description("자기소개 글")
                        )
                ));

    }

    @Test
    @DisplayName("회원 삭제")
    void deleteUser() throws Exception {
        User savedUser = userService.saveUser(createUser(), passwordEncoder);
        Long userId = savedUser.getId();

        String token = generateToken();
        mockMvc.perform(delete("/user/{userId}", userId)
                        .header(AUTHORIZATION, token)
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("user-delete",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(
                                removeHeaders("Vary", "X-Content-Type-Options", "X-XSS-Protection", "Cache-Control",
                                        "Pragma", "Expires",
                                        "Strict-Transport-Security", "X-Frame-Options"), prettyPrint()),
                        pathParameters(
                                parameterWithName("userId").description("회원 ID")
                        )
                ));
    }

    @Test
    @DisplayName("회원 아이디 중복 확인")
    public void duplicateUserId() throws Exception {
        User user = userService.saveUser(createUser(), passwordEncoder);
        mockMvc.perform(MockMvcRequestBuilders.post("/user/check?email=test1234@naver.com")
                        .contentType(APPLICATION_JSON)
//                        .param("email","test1234@naver.com") // rest docs에서 한글이 깨져서 url에 파라미터로 바로 넣었습니다.
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("user-check-email",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(
                                removeHeaders("Vary", "X-Content-Type-Options", "X-XSS-Protection", "Pragma", "Expires",
                                        "Cache-Control", "Strict-Transport-Security", "X-Frame-Options"),
                                prettyPrint()),
                        requestParameters(
                                parameterWithName("email").description("검증할 이메일")
                        )
                ));
    }

    @Test
    @DisplayName("비밀번호 검증")
    public void checkUserPassword() throws Exception {
        User user = userService.saveUser(createUser(), passwordEncoder);
        mockMvc.perform(RestDocumentationRequestBuilders
                        .post("/user/{userId}/check?password=1234", user.getId())
                        .header(AUTHORIZATION, generateToken())
                        .contentType(APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("user-check-password",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(
                                removeHeaders("Vary", "X-Content-Type-Options", "X-XSS-Protection", "Pragma", "Expires",
                                        "Cache-Control", "Strict-Transport-Security", "X-Frame-Options"),
                                prettyPrint()),
                        requestParameters(
                                parameterWithName("password").description("검증할 비밀번호")
                        ),
                        pathParameters(
                                parameterWithName("userId").description("회원 ID")
                        )
                ));
    }

    @Test
    @DisplayName("비밀번호 변경 테스트")
    public void changeUserPassword() throws Exception {
        User user = userService.saveUser(createUser(), passwordEncoder);
        mockMvc.perform(RestDocumentationRequestBuilders
                        .post("/user/{userId}/change?oldPassword=1234&newPassword=12345", user.getId())
                        .header(AUTHORIZATION, generateToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("user-change-password",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(
                                removeHeaders("Vary", "X-Content-Type-Options", "X-XSS-Protection", "Pragma", "Expires",
                                        "Cache-Control", "Strict-Transport-Security", "X-Frame-Options"),
                                prettyPrint()),
                        requestParameters(
                                parameterWithName("oldPassword").description("기존 비밀번호"),
                                parameterWithName("newPassword").description("변경할 비밀번호")
                        ),
                        pathParameters(
                                parameterWithName("userId").description("회원 ID")
                        )
                ));
    }

    @Test
    @DisplayName("회원정보 조회 테스트")
    public void getUserInfo() throws Exception {
        User user = userService.saveUser(createUser(), passwordEncoder);
        String token = generateToken();
        mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/user/{userId}", user.getId())
                        .header(AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.userName").value(user.getUserName()))
                .andExpect(jsonPath("$.info").value(user.getInfo()))
                .andExpect(jsonPath("$.imageUrl").value(user.getUserImg().getImgUrl()))
                .andDo(print())
                .andDo(document("user-info",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(
                                removeHeaders("Vary", "X-Content-Type-Options", "X-XSS-Protection", "Pragma", "Expires",
                                        "Cache-Control", "Strict-Transport-Security", "X-Frame-Options"),
                                prettyPrint()),
                        pathParameters(
                                parameterWithName("userId").description("회원 ID")
                        )
                ));
    }

    @Test
    @DisplayName("내가 작성한 챌린지 조회 테스트")
    public void getChallengeByUser() throws Exception {
        Challenge challenge = createChallenge();
        User user = userService.findByEmail(EMAIL).orElseThrow(UserNotFound::new);
        UserChallenge userChallenge = userChallengeService.saveUserChallenge(challenge, user);
        String token = generateToken();

        mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/user/challenge")
                        .header(AUTHORIZATION, token)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].challengeId").value(challenge.getId()))
                .andExpect(jsonPath("$[0].challengeTitle").value(challenge.getTitle()))
                .andExpect(jsonPath("$[0].challengeContent").value(challenge.getContent()))
                .andExpect(jsonPath("$[0].challengeStatus").value(userChallenge.getChallengeStatus().toString()))
                .andDo(document("user-my-challenge",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(
                                removeHeaders("Vary", "X-Content-Type-Options", "X-XSS-Protection", "Pragma", "Expires",
                                        "Cache-Control", "Strict-Transport-Security", "X-Frame-Options"),
                                prettyPrint())));
    }

    @Test
    @DisplayName("내가 참여한 챌린지 조회 테스트")
    public void getParticipateChallenge() throws Exception {
        Challenge challenge = createChallenge2();

        User savedUser = testDataSetup.saveUser(USERNAME, EMAIL, PASSWORD);

        testDataSetup.챌린지에_참가한다(challenge, savedUser);
        Comment comment = testDataSetup.챌린지에_댓글을_단다(challenge, savedUser, "content");
        testDataSetup.댓글에_이미지를_추가한다(comment);
        testDataSetup.댓글에_이미지를_추가한다(comment);
        testDataSetup.챌린지에_댓글을_단다(challenge, savedUser, null);

        mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/user/participate")
                        .header(AUTHORIZATION, generateToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("user-participate-challenge",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(
                                removeHeaders("Vary", "X-Content-Type-Options", "X-XSS-Protection", "Pragma", "Expires",
                                        "Cache-Control", "Strict-Transport-Security", "X-Frame-Options"),
                                prettyPrint())));
    }

    @Test
    @DisplayName("내가 진행중인 챌린지들 조회 테스트")
    public void getInProgressChallenges() throws Exception {
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

        mockMvc.perform(RestDocumentationRequestBuilders
                        .get("/user/inProgress")
                        .header(AUTHORIZATION, generateToken())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("user-inProgress-challenge",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(
                                removeHeaders("Vary", "X-Content-Type-Options", "X-XSS-Protection", "Pragma", "Expires",
                                        "Cache-Control", "Strict-Transport-Security", "X-Frame-Options"),
                                prettyPrint()),
                        relaxedResponseFields(
                                fieldWithPath("[].howManyDaysInProgress").description("챌린지에 참여한 날부터 오늘까지의 연속일수"),
                                fieldWithPath("[].weeklyAchievement").description("주간 챌린지 달성 여부")
                        ))
                );
    }

    private String generateToken() {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(EMAIL, PASSWORD));
        if (auth.isAuthenticated()) {
            UserDetails userDetails = userService.loadUserByUsername(EMAIL);
            return TOKEN_PREFIX + jwtTokenUtil.generateToken(userDetails);
        }

        throw new IllegalArgumentException("token 생성 오류");
    }
}
