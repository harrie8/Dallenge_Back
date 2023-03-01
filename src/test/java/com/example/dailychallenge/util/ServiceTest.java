package com.example.dailychallenge.util;

import com.example.dailychallenge.util.fixture.TestImgCleanup;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class ServiceTest {
    @Autowired
    protected TestImgCleanup testImgCleanup;
    @Autowired
    protected PasswordEncoder passwordEncoder;

    @AfterEach
    void afterEach() {
        testImgCleanup.afterPropertiesSet();
    }
}
