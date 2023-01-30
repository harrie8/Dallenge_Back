package com.example.dailychallenge.controller.challenge;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.dailychallenge.dto.UserDto;
import com.example.dailychallenge.entity.challenge.ChallengeStatus;
import com.example.dailychallenge.service.users.UserService;
import com.example.dailychallenge.utils.JwtTokenUtil;
import com.example.dailychallenge.vo.RequestCreateChallenge;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
    PasswordEncoder passwordEncoder;
    @Autowired
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;


    public UserDto createUser() {
        UserDto userDto = new UserDto();
        userDto.setEmail(EMAIL);
        userDto.setUserName("홍길동");
        userDto.setInfo("testInfo");
        userDto.setPassword(PASSWORD);
        return userDto;
    }

    MockMultipartFile createMultipartFiles() {
        String path = "challengeImgFile";
        String imageName = "challengeImage.jpg";
        return new MockMultipartFile(path, imageName,
                "image/jpg", new byte[]{1, 2, 3, 4});
    }

    @Test
    @DisplayName("챌린지 생성 테스트")
    public void createChallengeTest() throws Exception {
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
                "application/json", json.getBytes(UTF_8));

        MockPart tag1 = new MockPart("\"hashtagDto\"", "tag1".getBytes(UTF_8));
        MockPart tag2 = new MockPart("\"hashtagDto\"", "tag2".getBytes(UTF_8));

        String token = generateToken();
        mockMvc.perform(multipart("/challenge/new")
                        .file(challengeImgFile)
                        .file(requestCreateChallenge)
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
                .andDo(print());
    }

    @Test
    @DisplayName("존재하지 않는 카테고리로 챌린지 생성 테스트")
    public void createChallengeByCategoryNotFoundTest() throws Exception {
        userService.saveUser(createUser(), passwordEncoder);
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