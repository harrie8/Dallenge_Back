package com.example.dailychallenge.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.dailychallenge.dto.UserDto;
import com.example.dailychallenge.entity.User;
import com.example.dailychallenge.service.UserService;
import com.example.dailychallenge.vo.RequestUpdateUser;
import com.example.dailychallenge.vo.RequestUser;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.mock.web.MockPart;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Part;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class UserControllerTest {
    private final static String TOKEN_PREFIX = "Bearer ";
    private final static String TOKEN = "token";

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Value("${userImgLocation}")
    private String userImgLocation;


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
        RequestUpdateUser requestUpdateUser = RequestUpdateUser.builder()
                .userName("editName")
                .password("789")
                .info("editInfo")
                .build();

        MockMultipartFile userImgFile = createMultipartFiles();
        String data = objectMapper.writeValueAsString(requestUpdateUser);

        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/user/{userId}",userId);
        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod("PUT");
                return request;
            }
        });

        mockMvc.perform(builder
                        .file(userImgFile)
                        .param("data",data)
                .header("Authorization",getToken())
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());


//        mockMvc.perform(put("/user/{userId}", userId)
//                        .header("Authorization", getToken())
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(json)
//                        .accept(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andDo(print());

    }

    @Test
    @DisplayName("회원 삭제 api 테스트")
    public void deleteUserTest() throws Exception {
        User savedUser = userService.saveUser(createUser(), passwordEncoder);
        Long userId = savedUser.getId();

        mockMvc.perform(delete("/user/{userId}", userId)
                        .header("Authorization", getToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }

    private String getToken() throws Exception {
        Map<String, String> loginData = new HashMap<>();
        loginData.put("email", "test1234@test.com");
        loginData.put("password", "1234");

        ResultActions resultActions = mockMvc.perform(post("/user/login")
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