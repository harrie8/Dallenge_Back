package com.example.dailychallenge.controller;

import com.example.dailychallenge.dto.UserDto;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.exception.users.UserIdDuplicate;
import com.example.dailychallenge.exception.users.UserLoginFailure;
import com.example.dailychallenge.service.users.UserService;
import com.example.dailychallenge.utils.JwtTokenUtil;
import com.example.dailychallenge.vo.RequestLogin;
import com.example.dailychallenge.vo.RequestUpdateUser;
import com.example.dailychallenge.vo.RequestUser;
import com.example.dailychallenge.vo.ResponseLoginUser;
import com.example.dailychallenge.vo.ResponseUser;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;

    @PostMapping("/user/new")
    public ResponseEntity createUser(@RequestBody @Valid RequestUser requestUser) throws Exception {
        ModelMapper mapper = new ModelMapper();

        UserDto userDto = mapper.map(requestUser, UserDto.class);
        User savedUser = userService.saveUser(userDto, passwordEncoder);
        ResponseUser responseUser = mapper.map(userDto, ResponseUser.class);
        responseUser.setUserId(savedUser.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

    @PostMapping("/user/login")
    public ResponseEntity loginUser(@RequestBody @Valid RequestLogin requestLogin) {
        ModelMapper mapper = new ModelMapper();
        try {
            Authentication auth = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    requestLogin.getEmail(), requestLogin.getPassword()
            ));
            if (auth.isAuthenticated()) {
                UserDetails userDetails = userService.loadUserByUsername(requestLogin.getEmail());
                String token = jwtTokenUtil.generateToken(userDetails);
                User user = userService.findByEmail(requestLogin.getEmail());

                ResponseLoginUser responseLoginUser = mapper.map(user, ResponseLoginUser.class);
                responseLoginUser.setUserId(user.getId());
                responseLoginUser.setToken(token);

                return ResponseEntity.status(HttpStatus.OK).body(responseLoginUser);
            } else {
                /** 로그인 되지 않은 사용자인 경우  -> 소셜 로그인 이후 변경 예정 **/
                return ResponseEntity.status(401).body("Invalid Credentials");
            }
        } catch (Exception e) {
            // 아이디, 비밀번호 틀린 경우
            throw new UserLoginFailure();
        }
    }

    @PostMapping("/user/{userId}")
    public void updateUser(@PathVariable Long userId,
                           @RequestPart @Valid RequestUpdateUser requestUpdateUser,
                           @RequestPart(value = "userImgFile", required = false) MultipartFile multipartFile) throws Exception {

        userService.updateUser(userId, requestUpdateUser, passwordEncoder, multipartFile);
    }

    @DeleteMapping("/user/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.delete(userId);
    }

    @PostMapping("/user/check")
    public ResponseEntity<String> checkDuplicateUser(@RequestParam String email){
        User user = userService.findByEmail(email);
        if (user != null) {
            throw new UserIdDuplicate();
        }
        return ResponseEntity.status(200).body("사용 가능한 아이디입니다.");
    }


    /**
     * 2023-01-29
     * aws 구글 로그인 url : http://ec2-52-78-166-208.ap-northeast-2.compute.amazonaws.com:8080/oauth2/authorization/google
     * local 구글 로그인 url : http://localhost:8080/oauth2/authorization/google
     */
    @GetMapping("/api/user") // TEST
    public Authentication user(Authentication authentication, @AuthenticationPrincipal OAuth2User oAuth2User) {
        System.out.println("authentication = " + authentication + ", oAuth2User = " + oAuth2User);
        return authentication;
    }

}
