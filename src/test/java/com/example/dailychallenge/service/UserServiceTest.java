package com.example.dailychallenge.service;

import com.example.dailychallenge.dto.UserDto;
import com.example.dailychallenge.entity.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    ObjectMapper objectMapper;

    public UserDto createUser(){
        UserDto userDto = new UserDto();
        userDto.setEmail("test1234@test.com");
        userDto.setUserName("홍길동");
        userDto.setInfo("testInfo");
        userDto.setPassword("1234");
        return userDto;
    }

    @Test
    @DisplayName("회원가입 테스트")
    public void saveUserTest(){
        UserDto userDto = createUser();
        User savedUser = userService.saveUser(userDto,passwordEncoder);

        assertEquals(savedUser.getEmail(),userDto.getEmail());
        assertEquals(savedUser.getUserName(),userDto.getUserName());
        assertEquals(savedUser.getInfo(),userDto.getInfo());

        assertAll(
                () -> assertNotEquals(savedUser.getPassword(), userDto.getPassword()),
                () -> assertTrue(passwordEncoder.matches(userDto.getPassword(), savedUser.getPassword()))
        );
    }

    @Test
    @DisplayName("로그인 테스트")
    public void loginUserTest() throws Exception {
        Map<String, String> loginData = new HashMap<>();
        loginData.put("email", "test1234@test.com");
        loginData.put("password", "1234");

        userService.saveUser(createUser(),passwordEncoder);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginData))
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }
}