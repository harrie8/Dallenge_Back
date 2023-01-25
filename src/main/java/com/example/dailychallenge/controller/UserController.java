package com.example.dailychallenge.controller;

import com.example.dailychallenge.dto.UserDto;
import com.example.dailychallenge.entity.ProviderUser;
import com.example.dailychallenge.entity.User;
import com.example.dailychallenge.entity.social.OAuth2ProviderUser;
import com.example.dailychallenge.service.UserService;
import com.example.dailychallenge.utils.JwtTokenUtil;
import com.example.dailychallenge.vo.RequestLogin;
import com.example.dailychallenge.vo.RequestUpdateUser;
import com.example.dailychallenge.vo.RequestUser;
import com.example.dailychallenge.vo.ResponseLoginUser;
import com.example.dailychallenge.vo.ResponseUser;
import lombok.RequiredArgsConstructor;
import org.json.simple.parser.JSONParser;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.Map;

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
        User savedUser = userService.saveUser(userDto, passwordEncoder);
        ResponseUser responseUser = mapper.map(userDto, ResponseUser.class);
        responseUser.setUserId(savedUser.getId());

        return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
    }

    @PostMapping("/user/login")
    public ResponseEntity loginUser(@RequestBody RequestLogin requestLogin) {
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
                // 로그인 되지 않은 사용자인 경우
                return ResponseEntity.status(401).body("Invalid Credentials");
            }
        } catch (Exception e) {
            // 아이디, 비밀번호 틀린 경우
            return ResponseEntity.status(500).body("이메일 또는 비밀번호가 일치하지 않습니다.");
        }
    }

    @PostMapping("/user/{userId}")
    public void updateUser(@PathVariable Long userId,
//                           @RequestBody @Valid RequestUpdateUser requestUpdateUser,
                           @RequestParam("data") String updateData,
                           @RequestParam(value = "userImgFile") MultipartFile multipartFile
    ) throws Exception {

        // update user data 랑 image 같이 받으려고하다보니 parameter 로 받게 되었습니다
        JSONParser parser = new JSONParser();
        Object obj = parser.parse( updateData );
        RequestUpdateUser requestUpdateUser = new ModelMapper().map(obj, RequestUpdateUser.class);

        userService.updateUser(userId, requestUpdateUser, passwordEncoder, multipartFile);
    }

    @DeleteMapping("/user/{userId}")
    public void deleteUser(@PathVariable Long userId) {
        userService.delete(userId);
    }


    /**
     * 2023-01-25
     * google 로그인 구현했는데 현재 redirect url이 localhost:8080~ 으로 되어있어서
     * 로그인 url은 ip가 아닌 http://localhost:8080/oauth2/authorization/google 로 해주셔야 합니다
     * >> aws의 경우 http://ec2-52-78-166-208.ap-northeast-2.compute.amazonaws.com:8080/login/oauth2/code/google
     * 로그인(/회원가입) 성공하면 /api/user 로 넘어가서 데이터 확인합니다
     * db 저장까지 완료했는데 회원가입 이후 jwt 토큰을 통해 인증을 받을지 어쩔지 정해야할 것 같습니다
     * ++이미지 url을 받아올지 말지도 정해야할 것 같습니다
     */
    @GetMapping("/api/user") // TEST
    public Authentication user(Authentication authentication, @AuthenticationPrincipal OAuth2User oAuth2User) {
        System.out.println("authentication = " + authentication + ", oAuth2User = " + oAuth2User);
        return authentication;
    }

}
