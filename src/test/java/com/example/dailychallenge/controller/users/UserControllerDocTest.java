package com.example.dailychallenge.controller.users;

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
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestPartFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.dailychallenge.dto.UserDto;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.repository.UserRepository;
import com.example.dailychallenge.service.users.UserService;
import com.example.dailychallenge.utils.JwtTokenUtil;
import com.example.dailychallenge.vo.RequestLogin;
import com.example.dailychallenge.vo.RequestUpdateUser;
import com.example.dailychallenge.vo.RequestUser;
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

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.dailychallenge.com", uriPort = 443)
@ExtendWith(RestDocumentationExtension.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class UserControllerDocTest {
    private final static String TOKEN_PREFIX = "Bearer ";
    private final static String AUTHORIZATION = "Authorization";
    private final static String EMAIL = "test1234@test.com";
    private final static String PASSWORD = "1234";
    @Autowired
    UserRepository userRepository;
    @Autowired
    private UserService userService;
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
        userRepository.deleteAll();
    }

    public UserDto createUser() {
        UserDto userDto = new UserDto();
        userDto.setEmail("test1234@test.com");
        userDto.setUserName("홍길동");
        userDto.setInfo("testInfo");
        userDto.setPassword("1234");
        return userDto;
    }

    MockMultipartFile createMultipartFiles() throws Exception {
        String path = "userImgFile";
        String imageName = "editImage.jpg";
        MockMultipartFile multipartFile = new MockMultipartFile(path, imageName,
                "image/jpg", new byte[]{1, 2, 3, 4});
        return multipartFile;
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
                .password("789")
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
                                partWithName("requestUpdateUser").description("회원 정보 수정 데이터(JSON)").attributes(key("type").value("JSON")),
                                partWithName("userImgFile").description("회원 프로필 이미지(FILE)").optional().attributes(key("type").value(".jpg"))
                        ),
                        requestPartFields("requestUpdateUser",
                                fieldWithPath("userName").description("회원 이름"),
                                fieldWithPath("password").description("회원 비밀번호"),
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
                        .post("/user/{userId}/check?password=1234",user.getId())
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
                        .post("/user/{userId}/change?oldPassword=1234&newPassword=12345",user.getId())
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
