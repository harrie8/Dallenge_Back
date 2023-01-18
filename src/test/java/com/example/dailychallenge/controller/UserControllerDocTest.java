package com.example.dailychallenge.controller;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.snippet.Attributes.key;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.dailychallenge.dto.UserDto;
import com.example.dailychallenge.entity.User;
import com.example.dailychallenge.service.UserService;
import com.example.dailychallenge.vo.RequestLogin;
import com.example.dailychallenge.vo.RequestUpdateUser;
import com.example.dailychallenge.vo.RequestUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs(uriScheme = "https", uriHost = "api.dailychallenge.com", uriPort = 443)
@ExtendWith(RestDocumentationExtension.class)
@TestPropertySource(locations = "classpath:application-test.properties")
public class UserControllerDocTest {

    @Autowired
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ObjectMapper objectMapper;

    public UserDto createUser(){
        UserDto userDto = new UserDto();
        userDto.setEmail("test1234@test.com");
        userDto.setUserName("홍길동");
        userDto.setInfo("testInfo");
        userDto.setPassword("1234");
        return userDto;
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
                                fieldWithPath("userName").description("회원가입 성공한 username")
                        )
                ));
    }

    @Test
    @DisplayName("로그인")
    public void loginUser() throws Exception {
        userService.saveUser(createUser(),passwordEncoder);

        RequestLogin requestLogin = RequestLogin.builder()
                .email("test1234@test.com")
                .password("1234")
                .build();


        String json = objectMapper.writeValueAsString(requestLogin);

        mockMvc.perform(post("/login")
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

        String json = objectMapper.writeValueAsString(requestUpdateUser);
        mockMvc.perform(put("/user/{userId}", userId)
                        .header("authorization", "token")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                        .accept(APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print())
                .andDo(document("user-update",
                        pathParameters(
                                parameterWithName("userId").description("회원 ID")
                        ),
                        requestFields(
                                fieldWithPath("userName").description("이름")
                                        .attributes(key("constraint").value("회원 이름을 입력해주세요.")),
                                fieldWithPath("password").description("비밀번호")
                                        .attributes(key("constraint").value("회원 비밀번호를 입력해주세요.")),
                                fieldWithPath("info").description("소개글")
                                        .attributes(key("constraint").value("회원 소개글을 입력해주세요."))
                        )
                ));
    }

    @Test
    @DisplayName("회원 삭제")
    void deleteUser() throws Exception {
        User savedUser = userService.saveUser(createUser(), passwordEncoder);
        Long userId = savedUser.getId();

        mockMvc.perform(delete("/user/{userId}", userId)
                        .header("authorization", "token")
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
}
