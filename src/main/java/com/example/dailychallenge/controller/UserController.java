package com.example.dailychallenge.controller;

import com.example.dailychallenge.dto.UserDto;
import com.example.dailychallenge.service.UserService;
import com.example.dailychallenge.vo.RequestUpdateUser;
import com.example.dailychallenge.vo.RequestUser;
import com.example.dailychallenge.vo.ResponseUser;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/user/new")
    public ResponseEntity createUser(@RequestBody RequestUser requestUser) {
        ModelMapper mapper = new ModelMapper();

        UserDto userDto = mapper.map(requestUser, UserDto.class);
        userService.saveUser(userDto, passwordEncoder);
        ResponseUser responseUser = mapper.map(userDto, ResponseUser.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

    // login 은 spring securiy 활용 -> url: 127.0.0.1:8080/login

    /**
     * url에서 new, update, delete는 http 메서드로 대체 가능하지 않을까?
     * JWT token 값은 header에 임의로 설정해놨습니다
     */
    @PutMapping("/user/{userId}")
    public void createUser(@PathVariable Long userId,
                           @RequestBody @Valid RequestUpdateUser requestUpdateUser,
                           @RequestHeader String authorization) {

        if (authorization.equals("token")) {
            userService.updateUser(userId, requestUpdateUser, passwordEncoder);
        }
    }
}
