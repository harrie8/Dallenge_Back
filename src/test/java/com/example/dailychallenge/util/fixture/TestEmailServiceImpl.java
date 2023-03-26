package com.example.dailychallenge.util.fixture;

import com.example.dailychallenge.dto.EmailDto;
import com.example.dailychallenge.service.email.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@Profile("test")
public class TestEmailServiceImpl implements EmailService {

    public void sendMail(EmailDto emailDto) {
        log.info("emailDto");
        log.info("Email sending Success!!");
    }
}
