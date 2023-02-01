package com.example.dailychallenge.controller.challenge;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.removeHeaders;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.dailychallenge.dto.UserDto;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.challenge.ChallengeCategory;
import com.example.dailychallenge.entity.challenge.ChallengeDuration;
import com.example.dailychallenge.entity.challenge.ChallengeLocation;
import com.example.dailychallenge.entity.challenge.ChallengeStatus;
import com.example.dailychallenge.entity.challenge.UserChallenge;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.repository.ChallengeRepository;
import com.example.dailychallenge.repository.UserChallengeRepository;
import com.example.dailychallenge.repository.UserRepository;
import com.example.dailychallenge.service.challenge.ChallengeService;
import com.example.dailychallenge.service.users.UserService;
import com.example.dailychallenge.utils.JwtTokenUtil;
import com.example.dailychallenge.vo.RequestCreateChallenge;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.dailychallenge.com", uriPort = 443)
@ExtendWith(RestDocumentationExtension.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class ChallengeControllerDocTest {
    private final static String TOKEN_PREFIX = "Bearer ";
    private final static String AUTHORIZATION = "Authorization";
    private final static String EMAIL = "test1234@test.com";
    private final static String PASSWORD = "1234";

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private ChallengeService challengeService;
    @Autowired
    private ChallengeRepository challengeRepository;
    @Autowired
    private UserChallengeRepository userChallengeRepository;
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

    @BeforeEach
    void beforeEach() {
        userChallengeRepository.deleteAll();
        userRepository.deleteAll();
        challengeRepository.deleteAll();
    }

    public UserDto createUser() {
        UserDto userDto = new UserDto();
        userDto.setEmail("test1234@test.com");
        userDto.setUserName("홍길동");
        userDto.setInfo("testInfo");
        userDto.setPassword("1234");
        return userDto;
    }

    MockMultipartFile createMultipartFiles() {
        String path = "challengeImgFile";
        String imageName = "challengeImgFile.jpg";
        return new MockMultipartFile(path, imageName,
                "image/jpg", new byte[]{1, 2, 3, 4});
    }

    @Test
    @DisplayName("챌린지 생성")
    void createChallenge() throws Exception {
        userService.saveUser(createUser(), passwordEncoder);
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
                "application/json", json.getBytes(
                StandardCharsets.UTF_8));

        MockPart tag1 = new MockPart("\"hashtagDto\"", "tag1".getBytes(UTF_8));
        MockPart tag2 = new MockPart("\"hashtagDto\"", "tag2".getBytes(UTF_8));

        String challengeCategoryDescriptions = String.join(", ", ChallengeCategory.getDescriptions());
        String challengeLocationDescriptions = String.join(", ", ChallengeLocation.getDescriptions());
        String challengeDurationDescriptions = String.join(", ", ChallengeDuration.getDescriptions());
        String token = generateToken();
        mockMvc.perform(multipart("/challenge/new")
                        .file(requestCreateChallenge)
                        .file(challengeImgFile)
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
                .andDo(print())
                .andDo(document("challenge-create",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(
                                removeHeaders("Vary", "X-Content-Type-Options", "X-XSS-Protection", "Pragma", "Expires",
                                        "Cache-Control", "Strict-Transport-Security", "X-Frame-Options"),
                                prettyPrint()),
                        requestParts(
                                partWithName("requestCreateChallenge").description("챌린지 정보 데이터(JSON)")
                                        .attributes(key("type").value("JSON")),
                                partWithName("challengeImgFile").description("챌린지 이미지 파일(FILE)").optional()
                                        .attributes(key("type").value(".jpg")),
                                partWithName("\"hashtagDto\"").description("해시태그 데이터(LIST)").optional()
                                        .attributes(key("type").value("LIST"))
                        ),
                        requestPartFields("requestCreateChallenge",
                                fieldWithPath("title").description("제목"),
                                fieldWithPath("content").description("내용"),
                                fieldWithPath("challengeCategory").description("카테고리")
                                        .attributes(key("format").value(challengeCategoryDescriptions)),
                                fieldWithPath("challengeLocation").description("장소")
                                        .attributes(key("format").value(challengeLocationDescriptions)),
                                fieldWithPath("challengeDuration").description("기간")
                                        .attributes(key("format").value(challengeDurationDescriptions))
                        )
                ));
    }

    @Test
    @DisplayName("모든 챌린지 인기순으로 조회 테스트")
    public void searchAllChallengesSortByPopularTest() throws Exception {
        userService.saveUser(createUser(), passwordEncoder);
        Challenge challenge1 = Challenge.builder()
                .title("제목입니다.1")
                .content("내용입니다.1")
                .challengeCategory(ChallengeCategory.STUDY)
                .challengeLocation(ChallengeLocation.INDOOR)
                .challengeDuration(ChallengeDuration.WITHIN_TEN_MINUTES)
                .build();
        Challenge challenge2 = Challenge.builder()
                .title("제목입니다.2")
                .content("내용입니다.2")
                .challengeCategory(ChallengeCategory.ECONOMY)
                .challengeLocation(ChallengeLocation.OUTDOOR)
                .challengeDuration(ChallengeDuration.OVER_ONE_HOUR)
                .build();
        challengeRepository.save(challenge1);
        challengeRepository.save(challenge2);
        for (int i = 1; i <= 10; i++) {
            User user =  User.builder()
                    .userName("홍길동" + i)
                    .email(i + "@test.com")
                    .password("1234")
                    .build();
            userRepository.save(user);
            if (i <= 2) {
                UserChallenge userChallenge = UserChallenge.builder()
                        .challengeStatus(ChallengeStatus.TRYING)
                        .user(user)
                        .challenge(challenge1)
                        .build();
                userChallengeRepository.save(userChallenge);
            }
            if (3 <= i && i <= 7) {
                UserChallenge userChallenge = UserChallenge.builder()
                        .challengeStatus(ChallengeStatus.PAUSE)
                        .user(user)
                        .challenge(challenge2)
                        .build();
                userChallengeRepository.save(userChallenge);
            }
        }

        String token = generateToken();
        mockMvc.perform(get("/challenge")
                        .header(AUTHORIZATION, token)
                        .param("size", "20")
                        .param("page", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value(challenge2.getTitle()))
                .andExpect(jsonPath("$[0].content").value(challenge2.getContent()))
                .andExpect(jsonPath("$[0].challengeCategory").value(challenge2.getChallengeCategory().getDescription()))
                .andExpect(jsonPath("$[0].challengeLocation").value(challenge2.getChallengeLocation().getDescription()))
                .andExpect(jsonPath("$[0].challengeDuration").value(challenge2.getChallengeDuration().getDescription()))
                .andExpect(jsonPath("$[0].howManyUsersAreInThisChallenge").value(5))
                .andExpect(jsonPath("$[1].title").value(challenge1.getTitle()))
                .andExpect(jsonPath("$[1].content").value(challenge1.getContent()))
                .andExpect(jsonPath("$[1].challengeCategory").value(challenge1.getChallengeCategory().getDescription()))
                .andExpect(jsonPath("$[1].challengeLocation").value(challenge1.getChallengeLocation().getDescription()))
                .andExpect(jsonPath("$[1].challengeDuration").value(challenge1.getChallengeDuration().getDescription()))
                .andExpect(jsonPath("$[1].howManyUsersAreInThisChallenge").value(2))
                .andDo(print())
                .andDo(document("challenges-find-all",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(
                                removeHeaders("Vary", "X-Content-Type-Options", "X-XSS-Protection", "Pragma", "Expires",
                                        "Cache-Control", "Strict-Transport-Security", "X-Frame-Options"),
                                prettyPrint()),
                        requestParameters(
                                parameterWithName("size").description("페이지네이션 - 사이즈"),
                                parameterWithName("page").description("페이지네이션 - 페이지, 0번부터 시작합니다.")
                        )
                ));
    }

    @Test
    @DisplayName("챌린지들을 검색 조건으로 찾고 인기순으로 정렬하는 테스트")
    public void searchChallengesByConditionSortByPopularTest() throws Exception {
        userService.saveUser(createUser(), passwordEncoder);
        User user = User.builder()
                .userName("홍길동")
                .email("test@test.com")
                .password("1234")
                .build();
        userRepository.save(user);
        for (int i = 1; i <= 10; i++) {
            ChallengeCategory challengeCategory = ChallengeCategory.STUDY;
            if (i >= 6) {
                challengeCategory = ChallengeCategory.ECONOMY;
            }
            Challenge challenge = Challenge.builder()
                    .title("제목입니다." + i)
                    .content("내용입니다." + i)
                    .challengeCategory(challengeCategory)
                    .challengeLocation(ChallengeLocation.INDOOR)
                    .challengeDuration(ChallengeDuration.WITHIN_TEN_MINUTES)
                    .build();
            challengeRepository.save(challenge);
            UserChallenge userChallenge = UserChallenge.builder()
                    .challengeStatus(ChallengeStatus.TRYING)
                    .user(user)
                    .challenge(challenge)
                    .build();
            userChallengeRepository.save(userChallenge);
        }

        String token = generateToken();
        mockMvc.perform(get("/challenge/condition")
                        .header(AUTHORIZATION, token)
                        .param("title", "제목")
                        .param("category", "공부")
                        .param("size", "20")
                        .param("page", "0")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$..['title']").exists())
                .andExpect(jsonPath("$..['challengeCategory']").exists())
                .andDo(print())
                .andDo(document("challenges-find-by-condition",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(
                                removeHeaders("Vary", "X-Content-Type-Options", "X-XSS-Protection", "Pragma", "Expires",
                                        "Cache-Control", "Strict-Transport-Security", "X-Frame-Options"),
                                prettyPrint()),
                        requestParameters(
                                parameterWithName("title").description("찾고 싶은 챌린지 제목").optional(),
                                parameterWithName("category").description("찾고 싶은 챌린지 카테고리").optional(),
                                parameterWithName("size").description("페이지네이션 - 사이즈"),
                                parameterWithName("page").description("페이지네이션 - 페이지, 0번부터 시작합니다.")
                        )
                ));
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
