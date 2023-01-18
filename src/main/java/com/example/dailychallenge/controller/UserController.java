package com.example.dailychallenge.controller;

import com.example.dailychallenge.dto.UserDto;
import com.example.dailychallenge.entity.User;
import com.example.dailychallenge.repository.UserRepository;
import com.example.dailychallenge.service.UserService;
import com.example.dailychallenge.utils.JwtTokenUtil;
import com.example.dailychallenge.vo.RequestLogin;
import com.example.dailychallenge.vo.RequestUpdateUser;
import com.example.dailychallenge.vo.RequestUser;
import com.example.dailychallenge.vo.ResponseUser;

import javax.persistence.EntityNotFoundException;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping("/user/new")
    public ResponseEntity createUser(@RequestBody RequestUser requestUser) throws Exception {
        ModelMapper mapper = new ModelMapper();

        UserDto userDto = mapper.map(requestUser, UserDto.class);
        userService.saveUser(userDto, passwordEncoder);
        ResponseUser responseUser = mapper.map(userDto, ResponseUser.class);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

    @PostMapping("/user/login")
    public ResponseEntity<?> loginUser(@RequestBody RequestLogin requestLogin) {
        Map<String, Object> responseMap = new HashMap<>();
        try {
            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    requestLogin.getEmail(), requestLogin.getPassword()
            ));
            if (auth.isAuthenticated()) {
                UserDetails userDetails = userService.loadUserByUsername(requestLogin.getEmail());
                String token = jwtTokenUtil.generateToken(userDetails);
                responseMap.put("error", false);
                responseMap.put("message", "Logged In");
                responseMap.put("token", token);
                return ResponseEntity.ok(responseMap);
            } else {
                responseMap.put("error", true);
                responseMap.put("message", "Invalid Credentials");
                return ResponseEntity.status(401).body(responseMap);
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body(responseMap);
        }
    }

    /**
     * url에서 new, update, delete는 http 메서드로 대체 가능하지 않을까?
     * JWT token 값은 header에 임의로 설정해놨습니다
     *
     * 2023-01-18
     * jwt 검증하는 필터를 추가해서 token 값은 파라미터로 받아오지 않아도 될 것 같습니다
     * 다만, 로그인 시 토큰 발급 후 body 에 담아 보냅니다
     */
    @PutMapping("/user/{userId}")
    public void updateUser(@PathVariable Long userId,
                           @RequestBody @Valid RequestUpdateUser requestUpdateUser,
//                           @RequestParam("data") String updateData,
                           @RequestHeader String authorization
//                           @RequestParam("userImgFile") MultipartFile multipartFile
    ) throws Exception {

        // update user data 랑 image 같이 받으려고하다보니 parameter 로 받게 되었습니다
//        JSONParser parser = new JSONParser();
//        Object obj = parser.parse( updateData );
//        RequestUpdateUser requestUpdateUser = new ModelMapper().map(obj, RequestUpdateUser.class);

        userService.updateUser(userId, requestUpdateUser, passwordEncoder);
//        if (authorization.equals("token")) {
//        }
    }

    @DeleteMapping("/user/{userId}")
    public void deleteUser(@PathVariable Long userId,
                           @RequestHeader String authorization) {

        userService.delete(userId);
//        if (authorization.equals("token")) {
//        }
    }

}
