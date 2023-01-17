package com.example.dailychallenge.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.dailychallenge.dto.UserDto;
import com.example.dailychallenge.entity.User;
import com.example.dailychallenge.exception.UserNotFound;
import com.example.dailychallenge.repository.UserRepository;
import com.example.dailychallenge.vo.RequestUpdateUser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
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
    PasswordEncoder passwordEncoder;


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
        String path = "C:/spring_study/image/profile/";
        String imageName = "image.jpg";
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
        assertEquals(savedUser.getInfo(),userDto.getInfo());

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

        userService.updateUser(savedUser.getId(), requestUpdateUser, passwordEncoder);

        assertEquals(savedUser.getUserName(), requestUpdateUser.getUserName());
        assertEquals(savedUser.getInfo(), requestUpdateUser.getInfo());
        assertAll(
                () -> assertNotEquals(savedUser.getPassword(), requestUpdateUser.getPassword()),
                () -> assertTrue(passwordEncoder.matches(requestUpdateUser.getPassword(), savedUser.getPassword()))
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

        assertThatThrownBy(() -> userService.updateUser(userId, requestUpdateUser, passwordEncoder))
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
    }
}