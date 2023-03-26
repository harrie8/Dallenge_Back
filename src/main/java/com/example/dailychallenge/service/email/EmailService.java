package com.example.dailychallenge.service.email;

import com.example.dailychallenge.dto.EmailDto;

public interface EmailService {
    void sendMail(EmailDto emailDto);
}
