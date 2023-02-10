package com.example.dailychallenge.util.fixture;

import static com.example.dailychallenge.util.fixture.TokenFixture.EMAIL;
import static com.example.dailychallenge.util.fixture.TokenFixture.PASSWORD;

import com.example.dailychallenge.dto.UserDto;

public class UserFixture {

    public static UserDto createUser() {
        UserDto userDto = new UserDto();
        userDto.setEmail(EMAIL);
        userDto.setUserName("홍길동");
        userDto.setInfo("testInfo");
        userDto.setPassword(PASSWORD);
        return userDto;
    }

    public static UserDto createOtherUser() {
        UserDto userDto = new UserDto();
        userDto.setEmail("a@a.com");
        userDto.setUserName("김철수");
        userDto.setInfo("aInfo");
        userDto.setPassword("1234");
        return userDto;
    }
}
