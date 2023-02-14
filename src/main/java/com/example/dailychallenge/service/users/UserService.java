package com.example.dailychallenge.service.users;

import com.example.dailychallenge.dto.UserDto;
import com.example.dailychallenge.dto.UserEditor;
import com.example.dailychallenge.entity.challenge.Challenge;
import com.example.dailychallenge.entity.social.ProviderUser;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.entity.users.UserImg;
import com.example.dailychallenge.exception.AuthorizationException;
import com.example.dailychallenge.exception.users.UserDuplicateNotCheck;
import com.example.dailychallenge.exception.users.UserNotFound;
import com.example.dailychallenge.repository.UserRepository;
import com.example.dailychallenge.vo.RequestUpdateUser;
import com.example.dailychallenge.vo.ResponseUserChallenge;
import com.example.dailychallenge.vo.ResponseUserInfo;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service @Transactional
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final UserImgService userImgService;
    @Value("${userImgLocation}")
    private String userImgLocation;
    @Value("${defaultUserImgLocation}")
    private String defaultUserImgLocation;

    MultipartFile createMultipartFiles() throws Exception {
        String path = userImgLocation+ "/";
        String imageName = "image.jpg";
        MockMultipartFile multipartFile = new MockMultipartFile(path, imageName,
                "image/jpg", new byte[]{1, 2, 3, 4});
        return multipartFile;
    }

    private MultipartFile createDefaultMultipartFile() {
        try {
            return new MockMultipartFile("defaultUserImg", "defaultUserImg.png",
                    "image/png", new FileInputStream(defaultUserImgLocation));
//            이미지 출처
//            <a href="[https://www.flaticon.com/kr/free-icons/](https://www.flaticon.com/kr/free-icons/)" title="프로필 아이콘">프로필 아이콘 제작자: Freepik - Flaticon</a>
        } catch (IOException e) {
            throw new IllegalArgumentException("해당 경로에서 이미지를 찾을 수 없습니다.");
        }
    }

    public User saveUser(UserDto userDto, PasswordEncoder passwordEncoder) throws Exception {

        validateDuplicateUser(userDto.getEmail());

        User user = User.builder()
                .userName(userDto.getUserName())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .registrationId(userDto.getProvider())
                .build();

        userRepository.save(user);

        UserImg userImg = new UserImg();
        userImg.saveUser(user);
//        userImgService.saveUserImg(userImg, createMultipartFiles());
        userImgService.saveUserImg(userImg, createDefaultMultipartFile());

        return user;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username);

        if(user == null)
            throw new UsernameNotFoundException(username);

        return new org.springframework.security.core.userdetails.User(
                user.getEmail(), user.getPassword(),
                true, true, true, true,
                new ArrayList<>()
        );
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    public void updateUser(String loginUserEmail, Long userId, RequestUpdateUser requestUpdateUser,
                           MultipartFile userImgFile) {

        validateUser(loginUserEmail, userId);

        User findUser = userRepository.findById(userId)
                .orElseThrow(UserNotFound::new);

        userImgService.updateUserImg(findUser.getUserImg().getId(), userImgFile);

        UserEditor.UserEditorBuilder editorBuilder = findUser.toEditor();
        UserEditor userEditor = editorBuilder
                .userName(requestUpdateUser.getUserName())
                .info(requestUpdateUser.getInfo())
                .build();

        findUser.update(userEditor);
    }

    public void delete(String loginUserEmail, Long userId) {
        validateUser(loginUserEmail, userId);

        User findUser = userRepository.findById(userId)
                .orElseThrow(UserNotFound::new);

        userRepository.delete(findUser);
    }

    public void saveSocialUser(String registrationId, ProviderUser providerUser){
        User user = User.builder()
                .userName(providerUser.getUserName())
                .email(providerUser.getEmail())
                .password(providerUser.getPassword())
                .registrationId(registrationId)
                .build();

        userRepository.save(user);
    }

    public void validateDuplicateUser(String email) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            throw new UserDuplicateNotCheck();
        }
    }

    public boolean checkPassword(Long userId, String pw,PasswordEncoder passwordEncoder) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFound::new);
        return passwordEncoder.matches(pw, user.getPassword());
    }

    public void changePassword(Long userId, String newPassword, PasswordEncoder passwordEncoder) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFound::new);
        user.changePassword(passwordEncoder.encode(newPassword));
    }

    public ResponseUserInfo getUserInfo(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(UserNotFound::new);
        ResponseUserInfo userInfo = ResponseUserInfo.builder()
                .email(user.getEmail())
                .userName(user.getUserName())
                .info(user.getInfo())
                .imageUrl(user.getUserImg().getImgUrl())
                .build();
        return userInfo;
    }

    public List<ResponseUserChallenge> getChallengeByUser(Long userId){
        User user = userRepository.findById(userId).orElseThrow(UserNotFound::new);
        List<Challenge> challengeList = user.getChallenges();
        List<ResponseUserChallenge> userChallenge = new ArrayList<>();

        for (Challenge challenge : challengeList) {
            userChallenge.add(
                    ResponseUserChallenge.builder()
                    .userId(userId)
                    .challengeId(challenge.getId())
                    .challengeTitle(challenge.getTitle())
                    .challengeContent(challenge.getContent())
                    .build()
            );
        }
        return userChallenge;
    }

    public void validateUser(String loginUserEmail, Long userId) {
        User loginUser = findByEmail(loginUserEmail);
        if (loginUser == null) {
            throw new UserNotFound();
        }

        if (!loginUser.isSameId(userId)) {
            throw new AuthorizationException();
        }
    }
}
