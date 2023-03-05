package com.example.dailychallenge.util.fixture.user;

import static com.example.dailychallenge.util.fixture.TokenFixture.EMAIL;
import static com.example.dailychallenge.util.fixture.TokenFixture.PASSWORD;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import com.example.dailychallenge.dto.UserDto;
import com.example.dailychallenge.entity.users.User;
import java.util.ArrayList;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

public class UserFixture {

    public static final String USERNAME = "홍길동";
    public static final String OTHER_USERNAME = "김철수";
    public static final String OTHER_EMAIL = "a@a.com";

    public static UserDto createUser() {
        UserDto userDto = new UserDto();
        userDto.setEmail(EMAIL);
        userDto.setUserName(USERNAME);
        userDto.setInfo("testInfo");
        userDto.setPassword(PASSWORD);
        return userDto;
    }

    public static UserDto createOtherUser() {
        UserDto userDto = new UserDto();
        userDto.setEmail(OTHER_EMAIL);
        userDto.setUserName(OTHER_USERNAME);
        userDto.setInfo("aInfo");
        userDto.setPassword(PASSWORD);
        return userDto;
    }

    public static User createSpecificUser(String name, String email) {
        return User.builder()
                .userName(name)
                .email(email)
                .password(PASSWORD)
                .build();
    }

    public static UserDto createSpecificUserDto(String name, String email) {
        UserDto userDto = new UserDto();
        userDto.setEmail(email);
        userDto.setUserName(name);
        userDto.setInfo("info");
        userDto.setPassword(PASSWORD);
        return userDto;
    }

    public static RequestPostProcessor getRequestPostProcessor(User user) {
        return user(new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(),
                true, true, true, true,
                new ArrayList<>()
        ));
    }
}
