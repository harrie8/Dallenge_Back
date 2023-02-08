package com.example.dailychallenge.service.users;

import com.example.dailychallenge.dto.UserDto;
import com.example.dailychallenge.dto.UserEditor;
import com.example.dailychallenge.entity.social.ProviderUser;
import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.entity.users.UserImg;
import com.example.dailychallenge.exception.users.UserDuplicateNotCheck;
import com.example.dailychallenge.exception.users.UserNotFound;
import com.example.dailychallenge.repository.UserRepository;
import com.example.dailychallenge.vo.RequestUpdateUser;
import java.util.ArrayList;
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

    MultipartFile createMultipartFiles() throws Exception {
        String path = userImgLocation+ "/";
        String imageName = "image.jpg";
        MockMultipartFile multipartFile = new MockMultipartFile(path, imageName,
                "image/jpg", new byte[]{1, 2, 3, 4});
        return multipartFile;
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
        userImgService.saveUserImg(userImg, createMultipartFiles());

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


    public void updateUser(Long userId, RequestUpdateUser requestUpdateUser,
                           PasswordEncoder passwordEncoder,
                           MultipartFile userImgFile) throws Exception {
        User findUser = userRepository.findById(userId)
                .orElseThrow(UserNotFound::new);

        userImgService.updateUserImg(findUser.getUserImg().getId(), userImgFile);

        UserEditor.UserEditorBuilder editorBuilder = findUser.toEditor();
        UserEditor userEditor = editorBuilder
                .userName(requestUpdateUser.getUserName())
                .password(passwordEncoder.encode(requestUpdateUser.getPassword()))
                .info(requestUpdateUser.getInfo())
                .build();

        findUser.update(userEditor);
    }

    public void delete(Long userId) {
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
}
