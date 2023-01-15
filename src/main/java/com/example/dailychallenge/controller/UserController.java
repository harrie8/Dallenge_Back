package com.example.dailychallenge.controller;

import com.example.dailychallenge.dto.UserDto;
import com.example.dailychallenge.entity.UserImg;
import com.example.dailychallenge.service.UserImgService;
import com.example.dailychallenge.service.UserService;
import com.example.dailychallenge.vo.RequestUpdateUser;
import com.example.dailychallenge.vo.RequestUser;
import com.example.dailychallenge.vo.ResponseUser;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.JSONParser;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;


    @PostMapping("/user/new")
    public ResponseEntity createUser(@RequestBody RequestUser requestUser) throws Exception {
        ModelMapper mapper = new ModelMapper();

        UserDto userDto = mapper.map(requestUser, UserDto.class);
        userService.saveUser(userDto, passwordEncoder);
        ResponseUser responseUser = mapper.map(userDto, ResponseUser.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

    // login 은 spring security 활용 -> url: 127.0.0.1:8080/login

    /**
     * url에서 new, update, delete는 http 메서드로 대체 가능하지 않을까?
     * JWT token 값은 header에 임의로 설정해놨습니다
     */
    @PutMapping("/user/{userId}")
    public void updateUser(@PathVariable Long userId,
//                           @RequestBody @Valid RequestUpdateUser requestUpdateUser,
                           @RequestParam("data") String updateData,
                           @RequestHeader String authorization,
                           @RequestParam("userImgFile") MultipartFile multipartFile) throws Exception {

        // update user data 랑 image 같이 받으려고하다보니 parameter 로 받게 되었습니다
        JSONParser parser = new JSONParser();
        Object obj = parser.parse( updateData );
        RequestUpdateUser requestUpdateUser = new ModelMapper().map(obj, RequestUpdateUser.class);

        if (authorization.equals("token")) {
            userService.updateUser(userId, requestUpdateUser, passwordEncoder,multipartFile);
        }
    }

    @DeleteMapping("/user/{userId}")
    public void deleteUser(@PathVariable Long userId,
                           @RequestHeader String authorization) {

        if (authorization.equals("token")) {
            userService.delete(userId);
        }
    }

}
