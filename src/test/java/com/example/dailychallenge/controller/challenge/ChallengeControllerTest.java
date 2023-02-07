package com.example.dailychallenge.controller.challenge;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.dailychallenge.dto.ChallengeSearchCondition;
import com.example.dailychallenge.dto.UserDto;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.ChallengeCategory;
import com.example.dailychallenge.entity.challenge.ChallengeDuration;
import com.example.dailychallenge.entity.challenge.ChallengeImg;
import com.example.dailychallenge.entity.challenge.ChallengeLocation;
import com.example.dailychallenge.entity.challenge.ChallengeStatus;
import com.example.dailychallenge.entity.challenge.UserChallenge;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.repository.ChallengeImgRepository;
import com.example.dailychallenge.repository.ChallengeRepository;
import com.example.dailychallenge.repository.UserChallengeRepository;
import com.example.dailychallenge.repository.UserRepository;
import com.example.dailychallenge.service.challenge.UserChallengeService;
import com.example.dailychallenge.service.users.UserService;
import com.example.dailychallenge.utils.JwtTokenUtil;
import com.example.dailychallenge.vo.RequestCreateChallenge;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.stream.Stream;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class ChallengeControllerTest {
    private final static String TOKEN_PREFIX = "Bearer ";
    private final static String AUTHORIZATION = "Authorization";
    private final static String EMAIL = "test1234@test.com";
    private final static String PASSWORD = "1234";

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private UserService userService;
    @Autowired
    private ChallengeRepository challengeRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserChallengeRepository userChallengeRepository;
    @Autowired
    private UserChallengeService userChallengeService;
    @Autowired
    private ChallengeImgRepository challengeImgRepository;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    private User savedUser;

    @BeforeEach
    void beforeEach() throws Exception {
        userChallengeRepository.deleteAll();
        challengeImgRepository.deleteAll();
        challengeRepository.deleteAll();
        userRepository.deleteAll();

        initData();
    }

    public UserDto createUser() {
        UserDto userDto = new UserDto();
        userDto.setEmail(EMAIL);
        userDto.setUserName("홍길동");
        userDto.setInfo("testInfo");
        userDto.setPassword(PASSWORD);
        return userDto;
    }

    private static MockMultipartFile createMultipartFiles() {
        String path = "challengeImgFile";
        String imageName = "challengeImage.jpg";
        return new MockMultipartFile(path, imageName,
                "image/jpg", new byte[]{1, 2, 3, 4});
    }

    private void initData() throws Exception {
        savedUser = userService.saveUser(createUser(), passwordEncoder);

        Challenge challenge1 = Challenge.builder()
                .title("제목입니다.1")
                .content("내용입니다.1")
                .challengeCategory(ChallengeCategory.STUDY)
                .challengeLocation(ChallengeLocation.INDOOR)
                .challengeDuration(ChallengeDuration.WITHIN_TEN_MINUTES)
                .build();

        ChallengeImg challengeImg = new ChallengeImg();
        challengeImg.updateUserImg("oriImgName", "imgName", "imgUrl");
        challenge1.addChallengeImg(challengeImg);
        challenge1.setUser(savedUser);
        challengeRepository.save(challenge1);
        UserChallenge userChallenge1 = UserChallenge.builder()
                .challengeStatus(ChallengeStatus.TRYING)
                .users(savedUser)
                .challenge(challenge1)
                .build();
        userChallengeRepository.save(userChallenge1);

        Challenge challenge2 = Challenge.builder()
                .title("제목입니다.2")
                .content("내용입니다.2")
                .challengeCategory(ChallengeCategory.ECONOMY)
                .challengeLocation(ChallengeLocation.OUTDOOR)
                .challengeDuration(ChallengeDuration.OVER_ONE_HOUR)
                .build();
        challenge2.setUser(savedUser);
        challengeRepository.save(challenge2);
        UserChallenge userChallenge2 = UserChallenge.builder()
                .challengeStatus(ChallengeStatus.PAUSE)
                .users(savedUser)
                .challenge(challenge2)
                .build();
        userChallengeRepository.save(userChallenge2);

        Challenge challenge6 = null;

        for (int i = 3; i <= 10; i++) {
            Challenge challenge = Challenge.builder()
                    .title("제목입니다." + i)
                    .content("내용입니다." + i)
                    .challengeCategory(ChallengeCategory.WORKOUT)
                    .challengeLocation(ChallengeLocation.INDOOR)
                    .challengeDuration(ChallengeDuration.WITHIN_TEN_MINUTES)
                    .build();
            challenge.setUser(savedUser);
            challengeRepository.save(challenge);
            UserChallenge userChallenge = UserChallenge.builder()
                    .challengeStatus(ChallengeStatus.TRYING)
                    .users(savedUser)
                    .challenge(challenge)
                    .build();
            userChallengeRepository.save(userChallenge);

            if (i == 6) {
                challenge6 = challenge;
            }
        }

        for (int i = 1; i <= 8; i++) {
            User user = User.builder()
                    .userName("홍길동" + i)
                    .email(i + "@test.com")
                    .password("1234")
                    .build();
            userRepository.save(user);
            if (i == 1) {
                UserChallenge userChallenge = UserChallenge.builder()
                        .challengeStatus(ChallengeStatus.TRYING)
                        .users(user)
                        .challenge(challenge1)
                        .build();
                userChallengeRepository.save(userChallenge);
            }
            if (2 <= i && i <= 5) {
                UserChallenge userChallenge = UserChallenge.builder()
                        .challengeStatus(ChallengeStatus.PAUSE)
                        .users(user)
                        .challenge(challenge2)
                        .build();
                userChallengeRepository.save(userChallenge);
            }

            if (i == 6) {
                UserChallenge userChallenge = UserChallenge.builder()
                        .challengeStatus(ChallengeStatus.TRYING)
                        .users(user)
                        .challenge(challenge6)
                        .build();
                userChallengeRepository.save(userChallenge);
            }
        }
    }

    // TODO: 2023-02-07 이미지 파일 없는 테스트 코드도 추가하기
    @Test
    @DisplayName("챌린지 생성 테스트")
    public void createChallengeTest() throws Exception {
        RequestCreateChallenge requestCreatChallenge = RequestCreateChallenge.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .challengeCategory("공부")
                .challengeLocation("실내")
                .challengeDuration("10분 이내")
                .build();
        MockMultipartFile challengeImgFile = createMultipartFiles();

        String json = objectMapper.writeValueAsString(requestCreatChallenge);
        MockMultipartFile requestCreateChallenge = new MockMultipartFile("requestCreateChallenge",
                "requestCreateChallenge",
                "application/json", json.getBytes(UTF_8));

        MockPart tag1 = new MockPart("\"hashtagDto\"", "tag1".getBytes(UTF_8));
        MockPart tag2 = new MockPart("\"hashtagDto\"", "tag2".getBytes(UTF_8));
        // TODO: 2023-02-07 현재 hashtag 값이 안 넘어와서  hashtag 테스트 코드 수정하기

        String token = generateToken();
        mockMvc.perform(multipart("/challenge/new")
                        .file(requestCreateChallenge)
                        .part(new MockPart("challengeImgFiles", "challengeImgFile", challengeImgFile.getBytes()))
                        .part(new MockPart("challengeImgFiles", "challengeImgFile", challengeImgFile.getBytes()))
                        .part(tag1)
                        .part(tag2)
                        .header(AUTHORIZATION, token)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value(requestCreatChallenge.getTitle()))
                .andExpect(jsonPath("$.content").value(requestCreatChallenge.getContent()))
                .andExpect(jsonPath("$.challengeCategory").value(requestCreatChallenge.getChallengeCategory()))
                .andExpect(jsonPath("$.challengeLocation").value(requestCreatChallenge.getChallengeLocation()))
                .andExpect(jsonPath("$.challengeDuration").value(requestCreatChallenge.getChallengeDuration()))
                .andExpect(jsonPath("$.challengeStatus").value(ChallengeStatus.TRYING.getDescription()))
                .andExpect(jsonPath("$.challengeImgUrls[*]").isNotEmpty())
                .andExpect(jsonPath("$.challengeOwnerUser.userName").value(savedUser.getUserName()))
                .andExpect(jsonPath("$.challengeOwnerUser.email").value(savedUser.getEmail()))
                .andExpect(jsonPath("$.challengeOwnerUser.userId").value(savedUser.getId()))
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 카테고리로 챌린지 생성 테스트")
    public void createChallengeByCategoryNotFoundTest() throws Exception {
        RequestCreateChallenge requestCreatChallenge = RequestCreateChallenge.builder()
                .title("제목입니다.")
                .content("내용입니다.")
                .challengeCategory("error")
                .challengeLocation("실내")
                .challengeDuration("10분 이내")
                .build();
        String json = objectMapper.writeValueAsString(requestCreatChallenge);
        MockMultipartFile requestCreateChallenge = new MockMultipartFile("requestCreateChallenge",
                "requestCreateChallenge",
                "application/json", json.getBytes(UTF_8));

        String token = generateToken();
        mockMvc.perform(multipart("/challenge/new")
                        .file(requestCreateChallenge)
                        .header(AUTHORIZATION, token)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(400))
                .andExpect(jsonPath("$.message").value("존재하지 않는 챌린지 카테고리입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("모든 챌린지 조회 테스트")
    public void searchAllChallengesTest() throws Exception {
        String token = generateToken();
        mockMvc.perform(get("/challenge")
                        .header(AUTHORIZATION, token)
                        .param("size", "20")
                        .param("page", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].title", contains(
                        "제목입니다.2", "제목입니다.1", "제목입니다.6", "제목입니다.3", "제목입니다.4",
                        "제목입니다.5", "제목입니다.7", "제목입니다.8", "제목입니다.9", "제목입니다.10")))
                .andExpect(jsonPath("$.content[*].content", contains(
                        "내용입니다.2", "내용입니다.1", "내용입니다.6", "내용입니다.3", "내용입니다.4",
                        "내용입니다.5", "내용입니다.7", "내용입니다.8", "내용입니다.9", "내용입니다.10")))
                .andExpect(jsonPath("$.content[*].challengeCategory",
                        hasItems(ChallengeCategory.ECONOMY.getDescription(), ChallengeCategory.STUDY.getDescription(),
                                ChallengeCategory.WORKOUT.getDescription())))
                .andExpect(jsonPath("$.content[*].challengeLocation",
                        hasItems(ChallengeLocation.OUTDOOR.getDescription(),
                                ChallengeLocation.INDOOR.getDescription())))
                .andExpect(jsonPath("$.content[*].challengeDuration",
                        hasItems(ChallengeDuration.OVER_ONE_HOUR.getDescription(),
                                ChallengeDuration.WITHIN_TEN_MINUTES.getDescription())))
                .andExpect(jsonPath("$.content[*].challengeImgUrls",
                        hasItems(List.of(), List.of("imgUrl"))))
                .andExpect(jsonPath("$.content[*].howManyUsersAreInThisChallenge",
                        contains(5, 2, 2, 1, 1, 1, 1, 1, 1, 1)))
                .andExpect(jsonPath("$.content[*].challengeOwnerUser.userName",
                        hasItem(savedUser.getUserName())))
                .andExpect(jsonPath("$.content[*].challengeOwnerUser.email",
                        hasItem(savedUser.getEmail())))
                .andExpect(jsonPath("$.content[*].challengeOwnerUser.userId",
                        hasItem(savedUser.getId().intValue())))
                .andDo(print());
    }

    static Stream<Arguments> generateConditionData() {
        return Stream.of(
                Arguments.of(ChallengeSearchCondition.builder()
                                .title("1").category(null).build(),
                        "popular",
                        List.of(
                                contains("제목입니다.1", "제목입니다.10"),
                                contains("내용입니다.1", "내용입니다.10"),
                                contains(ChallengeCategory.STUDY.getDescription(),
                                        ChallengeCategory.WORKOUT.getDescription()),
                                hasItem(ChallengeLocation.INDOOR.getDescription()),
                                hasItem(ChallengeDuration.WITHIN_TEN_MINUTES.getDescription()),
                                contains(List.of("imgUrl"), List.of()),
                                contains(2, 1)
                        )),
                Arguments.of(ChallengeSearchCondition.builder()
                                .title(null).category(ChallengeCategory.WORKOUT.getDescription()).build(),
                        "popular",
                        List.of(
                                contains("제목입니다.6", "제목입니다.3", "제목입니다.4", "제목입니다.5", "제목입니다.7", "제목입니다.8",
                                        "제목입니다.9", "제목입니다.10"),
                                contains("내용입니다.6", "내용입니다.3", "내용입니다.4", "내용입니다.5", "내용입니다.7", "내용입니다.8",
                                        "내용입니다.9", "내용입니다.10"),
                                hasItem(ChallengeCategory.WORKOUT.getDescription()),
                                hasItem(ChallengeLocation.INDOOR.getDescription()),
                                hasItem(ChallengeDuration.WITHIN_TEN_MINUTES.getDescription()),
                                hasItem(List.of()),
                                contains(2, 1, 1, 1, 1, 1, 1, 1)
                        )),
                Arguments.of(ChallengeSearchCondition.builder()
                                .title(null).category(ChallengeCategory.WORKOUT.getDescription()).build(),
                        "time",
                        List.of(
                                contains("제목입니다.3", "제목입니다.4", "제목입니다.5", "제목입니다.6", "제목입니다.7", "제목입니다.8",
                                        "제목입니다.9", "제목입니다.10"),
                                contains("내용입니다.3", "내용입니다.4", "내용입니다.5", "내용입니다.6", "내용입니다.7", "내용입니다.8",
                                        "내용입니다.9", "내용입니다.10"),
                                hasItem(ChallengeCategory.WORKOUT.getDescription()),
                                hasItem(ChallengeLocation.INDOOR.getDescription()),
                                hasItem(ChallengeDuration.WITHIN_TEN_MINUTES.getDescription()),
                                hasItem(List.of()),
                                contains(1, 1, 1, 2, 1, 1, 1, 1)
                        )),
                Arguments.of(ChallengeSearchCondition.builder()
                                .title("1").category(ChallengeCategory.STUDY.getDescription()).build(),
                        "popular",
                        List.of(
                                contains("제목입니다.1"),
                                contains("내용입니다.1"),
                                contains(ChallengeCategory.STUDY.getDescription()),
                                contains(ChallengeLocation.INDOOR.getDescription()),
                                contains(ChallengeDuration.WITHIN_TEN_MINUTES.getDescription()),
                                contains(List.of("imgUrl")),
                                contains(2))
                ));
    }

    @ParameterizedTest
    @MethodSource("generateConditionData")
    @DisplayName("챌린지들을 검색 조건으로 찾는 테스트")
    public void searchChallengesByConditionTest(ChallengeSearchCondition condition, String sortProperties,
                                                List<Matcher<Iterable<? extends String>>> expects) throws Exception {
        String token = generateToken();
        mockMvc.perform(get("/challenge/condition")
                        .header(AUTHORIZATION, token)
                        .param("title", condition.getTitle())
                        .param("category", condition.getCategory())
                        .param("size", "20")
                        .param("page", "0")
                        .param("sort", sortProperties)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[*].title", expects.get(0)))
                .andExpect(jsonPath("$.content[*].content", expects.get(1)))
                .andExpect(jsonPath("$.content[*].challengeCategory", expects.get(2)))
                .andExpect(jsonPath("$.content[*].challengeLocation", expects.get(3)))
                .andExpect(jsonPath("$.content[*].challengeDuration", expects.get(4)))
                .andExpect(jsonPath("$.content[*].challengeImgUrls", expects.get(5)))
                .andExpect(jsonPath("$.content[*].howManyUsersAreInThisChallenge", expects.get(6)))
                .andExpect(jsonPath("$.content[*].challengeOwnerUser.userName", hasItem(
                        savedUser.getUserName())))
                .andExpect(jsonPath("$.content[*].challengeOwnerUser.email", hasItem(
                        savedUser.getEmail())))
                .andExpect(jsonPath("$.content[*].challengeOwnerUser.userId", hasItem(
                        savedUser.getId().intValue())))
                .andDo(print());
    }

    @ParameterizedTest
    @CsvSource(value = {
            "10, 2"
    })
    @DisplayName("모든 챌린지 조회 페이징 테스트")
    public void searchAllChallengesPagingTest(int totalElements, int numOfPage) throws Exception {
        String token = generateToken();
        mockMvc.perform(get("/challenge")
                        .header(AUTHORIZATION, token)
                        .param("size", String.valueOf(numOfPage))
                        .param("page", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(totalElements))
                .andExpect(jsonPath("$.totalPages").value(totalElements / numOfPage))
                .andDo(print());
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