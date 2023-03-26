package com.example.dailychallenge.service.email;

import com.example.dailychallenge.dto.EmailDto;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
@Profile("local")
public class EmailServiceImpl implements EmailService{

    private final JavaMailSender javaMailSender;

    public void sendMail(EmailDto emailDto) {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(emailDto.getTo());
            mimeMessageHelper.setSubject(emailDto.makeSubject());
            mimeMessageHelper.setText(emailDto.makeText(), true);
            javaMailSender.send(mimeMessage);
            log.info("Email sending Success!!");
        } catch (MessagingException e) {
            log.error(e.getMessage());
            throw new RuntimeException(e);
        }
    }
}


