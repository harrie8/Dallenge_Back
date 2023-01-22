package com.example.dailychallenge.controller;

import com.example.dailychallenge.dto.UserDto;
import com.example.dailychallenge.entity.User;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

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

    /***
     * 2023-01-20 ** File Upload
     * 파일 업로드 bytes 안넘기고 multipart 로 넘기는 걸로 수정했습니다 !
     * 경로는 이제 코드 상에서 수정할 필요없고 properties 부분만 변경해서 테스트 하시면 될 것 같습니다
     * postman 에서 요청 시 form-data 로 요청하여 동작 확인하였습니다
     *
     * ** Issue
     * ControllerRestDocsTest 에 적어놨는데,
     * multipart()로 요청 시 디폴트 메서드가 POST 라서 에러 발생
     * put()으로 요청 시 file upload 가 불가합니다 ㅜㅜ 계속 찾아는 보고 있는데 잘 안되서 일단 주석 달아놨습니다 !
     */
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

}
