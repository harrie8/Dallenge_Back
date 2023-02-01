package com.example.dailychallenge.service.users;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import com.example.dailychallenge.dto.UserDto;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.exception.users.UserDuplicateNotCheck;
import com.example.dailychallenge.exception.users.UserNotFound;
import com.example.dailychallenge.exception.users.UserPasswordCheck;
import com.example.dailychallenge.repository.UserImgRepository;
import com.example.dailychallenge.repository.UserRepository;
import com.example.dailychallenge.vo.RequestUpdateUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class UserServiceTest {

    @Autowired
    private UserService userService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserImgRepository userImgRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Value("${userImgLocation}")
    private String userImgLocation;


    public UserDto createUser(){
        UserDto userDto = new UserDto();
        userDto.setEmail("test1234@test.com");
        userDto.setUserName("홍길동");
        userDto.setInfo("testInfo");
        userDto.setPassword("1234");
        return userDto;
    }

    /** 프로필 이미지 추가한 후 **/
    MultipartFile createMultipartFiles() throws Exception {
        String path = userImgLocation+"/";
        String imageName = "editImage.jpg";
        MockMultipartFile multipartFile = new MockMultipartFile(path, imageName,
                "image/jpg", new byte[]{1, 2, 3, 4});
        return multipartFile;
    }

    @Test
    @DisplayName("회원가입 테스트")
    public void saveUserTest() throws Exception {
        UserDto userDto = createUser();
        User savedUser = userService.saveUser(userDto,passwordEncoder);

        assertEquals(savedUser.getEmail(),userDto.getEmail());
        assertEquals(savedUser.getUserName(),userDto.getUserName());

        assertAll(
                () -> assertNotEquals(savedUser.getPassword(), userDto.getPassword()),
                () -> assertTrue(passwordEncoder.matches(userDto.getPassword(), savedUser.getPassword()))
        );
    }


    @Test
    @DisplayName("회원 정보 수정 테스트")
    public void updateUserTest() throws Exception {
        User savedUser = userService.saveUser(createUser(), passwordEncoder);
        MultipartFile multipartFile = createMultipartFiles();
        RequestUpdateUser requestUpdateUser = RequestUpdateUser.builder()
                .userName("editName")
                .info("editInfo")
                .password("789")
                .build();

        userService.updateUser(savedUser.getId(), requestUpdateUser, passwordEncoder, multipartFile);

        User editUser = userService.findByEmail(savedUser.getEmail());
        System.out.println("editUser>>>"+editUser.toString());

        assertEquals(editUser.getUserName(), requestUpdateUser.getUserName());
        assertEquals(editUser.getInfo(), requestUpdateUser.getInfo());
        assertAll(
                () -> assertNotEquals(editUser.getPassword(), requestUpdateUser.getPassword()),
                () -> assertTrue(passwordEncoder.matches(requestUpdateUser.getPassword(), editUser.getPassword()))
        );
    }

    @Test
    @DisplayName("존재하지 않는 회원 정보 수정 테스트")
    public void updateNotExistUser() throws Exception {
        User savedUser = userService.saveUser(createUser(), passwordEncoder);
        MultipartFile multipartFile = createMultipartFiles();
        RequestUpdateUser requestUpdateUser = RequestUpdateUser.builder()
                .userName("editName")
                .info("editInfo")
                .password("789")
                .build();
        Long userId = savedUser.getId() + 1;

        assertThatThrownBy(() -> userService.updateUser(userId, requestUpdateUser, passwordEncoder, multipartFile))
                .isInstanceOf(UserNotFound.class)
                .hasMessage("존재하지 않는 회원입니다.");
    }

    @Test
    @DisplayName("회원 삭제 테스트")
    public void deleteUserTest() throws Exception {
        User savedUser = userService.saveUser(createUser(), passwordEncoder);
        Long userId = savedUser.getId();

        userService.delete(userId);

        assertEquals(0, userRepository.count());
        assertEquals(0,userImgRepository.count());
    }

    @Test
    @DisplayName("회원 중복 에러 테스트")
    public void duplicateUserTest() throws Exception {
        User savedUser = userService.saveUser(createUser(), passwordEncoder);
        assertThatThrownBy(() -> userService.validateDuplicateUser(savedUser.getEmail()))
                .isInstanceOf(UserDuplicateNotCheck.class)
                .hasMessage("아이디 중복체크를 해주세요.");
    }

    @Test
    @DisplayName("비밀번호 검증 테스트")
    public void checkUserPassword() throws Exception {
        User savedUser = userService.saveUser(createUser(), passwordEncoder);

        assertTrue(userService.checkPassword(savedUser.getId(), "1234", passwordEncoder));
        assertFalse(userService.checkPassword(savedUser.getId(), "12345", passwordEncoder));
    }
}