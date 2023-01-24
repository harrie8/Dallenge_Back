package com.example.dailychallenge.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.dailychallenge.dto.UserDto;
import com.example.dailychallenge.entity.User;
import com.example.dailychallenge.repository.UserRepository;
import com.example.dailychallenge.service.UserService;
import com.example.dailychallenge.vo.RequestLogin;
import com.example.dailychallenge.vo.RequestUpdateUser;
import com.example.dailychallenge.vo.RequestUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.dailychallenge.com", uriPort = 443)
@ExtendWith(RestDocumentationExtension.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class UserControllerDocTest{
    private final static String TOKEN_PREFIX = "Bearer ";
    private final static String TOKEN = "token";

    @Autowired
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    UserRepository userRepository;

    @Value("${userImgLocation}")
    private String userImgLocation;


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
                        requestFields(
                                fieldWithPath("userName").description("이름")
                                        .attributes(key("constraint").value("회원 이름을 입력해주세요.")),
                                fieldWithPath("email").description("이메일")
                                        .attributes(key("constraint").value("회원 이메일을 입력해주세요.")),
                                fieldWithPath("password").description("비밀번호")
                                        .attributes(key("constraint").value("회원 비밀번호를 입력해주세요."))
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
                        requestFields(
                                fieldWithPath("email").description("이메일")
                                        .attributes(key("constraint").value("회원 이메일을 입력해주세요.")),
                                fieldWithPath("password").description("비밀번호")
                                        .attributes(key("constraint").value("회원 비밀번호를 입력해주세요."))
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

        RequestUpdateUser requestUpdateUser = RequestUpdateUser.builder()
                .userName("editName")
                .password("789")
                .info("editInfo")
                .build();

        MockMultipartFile userImgFile = createMultipartFiles();
        String data = objectMapper.writeValueAsString(requestUpdateUser);

        mockMvc.perform(multipart("/user/{userId}", userId)
                        .file(userImgFile)
                        .param("data", data)
                        .header("Authorization", getToken())
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("user-update",
                        pathParameters(
                                parameterWithName("userId").description("회원 ID")
                        ),
                        requestParts(
                                partWithName("userImgFile").description("회원 프로필 이미지").optional()
                        ),
                        requestParameters(
                                parameterWithName("data").description("회원 정보 수정 데이터")
                        )
                ));

//        mockMvc.perform(put("/user/{userId}",userId)
//            /** 파일 넣어줘야 하는데 multipart는 디폴트가 post 전송, put으로는 파잍 전송 X => 어떡하죠... **/
//                    .param("data",data)
//                    .header("Authorization",getToken())
//                    .contentType(MULTIPART_FORM_DATA)
//                    .accept(APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(print())
//                .andDo(document("user-update",
//                        pathParameters(
//                                parameterWithName("userId").description("회원 ID")
//                        ),
//                        requestParameters(
//                                parameterWithName("data").description("editData"),
//                                parameterWithName("userImgFile").description("회원 프로필 이미지").optional()
//                        )));

//        mockMvc.perform(put("/user/{userId}", userId)
//                        .header("Authorization", getToken())
//                        .contentType(APPLICATION_JSON)
//                        .content(data)
//                        .accept(APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(print())
//                .andDo(document("user-update",
//                        pathParameters(
//                                parameterWithName("userId").description("회원 ID")
//                        ),
//                        requestFields(
//                                fieldWithPath("userName").description("이름")
//                                        .attributes(key("constraint").value("회원 이름을 입력해주세요.")),
//                                fieldWithPath("password").description("비밀번호")
//                                        .attributes(key("constraint").value("회원 비밀번호를 입력해주세요.")),
//                                fieldWithPath("info").description("소개글")
//                                        .attributes(key("constraint").value("회원 소개글을 입력해주세요."))
//                        )
//                ));
    }

    @Test
    @DisplayName("회원 삭제")
    void deleteUser() throws Exception {
        User savedUser = userService.saveUser(createUser(), passwordEncoder);
        Long userId = savedUser.getId();

        mockMvc.perform(delete("/user/{userId}", userId)
                        .header("Authorization", getToken())
                        .contentType(APPLICATION_JSON)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("user-delete",
                        pathParameters(
                                parameterWithName("userId").description("회원 ID")
                        )
                ));
    }

    // 회원 정보 수정, 회원 삭제할 때 헤더에 token값을 주기 위한 메서드입니다.
    private String getToken() throws Exception {
        Map<String, String> loginData = new HashMap<>();
        loginData.put("email", "test1234@test.com");
        loginData.put("password", "1234");

        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.post("/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginData))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        String contentAsString = resultActions.andReturn().getResponse().getContentAsString();
        JSONParser jsonParser = new JSONParser();
        JSONObject jsonObject = (JSONObject) jsonParser.parse(contentAsString);
        String token = (String) jsonObject.get(TOKEN);

        return TOKEN_PREFIX + token;
    }

}
