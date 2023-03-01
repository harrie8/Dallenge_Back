package com.example.dailychallenge.util.fixture;

import com.example.dailychallenge.entity.users.User;
import com.example.dailychallenge.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class TestDataSetup {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Transactional
    public User saveUser(String userName, String email, String password) {
        return userRepository.save(User.builder()
                .userName(userName)
                .email(email)
                .password(passwordEncoder.encode(password))
                .build());
    }
}
