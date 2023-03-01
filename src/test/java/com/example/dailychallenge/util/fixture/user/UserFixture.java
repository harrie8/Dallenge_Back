package com.example.dailychallenge.util.fixture.user;

import static com.example.dailychallenge.util.fixture.TokenFixture.EMAIL;
import static com.example.dailychallenge.util.fixture.TokenFixture.PASSWORD;

import com.example.dailychallenge.dto.UserDto;
import com.example.dailychallenge.entity.users.User;

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
}
