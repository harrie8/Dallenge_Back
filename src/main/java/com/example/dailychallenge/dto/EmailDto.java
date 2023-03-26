package com.example.dailychallenge.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailDto {

    private String to;
    private String subject;
    private String message;

    @Builder
    public EmailDto(String to, String subject, String message) {
        this.to = to;
        this.subject = subject;
        this.message = message;
    }

    public String makeSubject() {
        return "[Dallenge] " + subject + "님, 임시 비밀번호를 발송해 드립니다.";
    }

    public String makeText() {
        return "<h3 style='color:grey; text-align:center;'>임시 비밀번호 발급</h3>"
                + "<p style='padding:15px; background:#eee; border-radius:5px; text-align:center;'><b>" + message
                + "</b></p>"
                + "<p style='text-align:center;'>임시 비밀번호가 발급되었습니다.</p>"
                + "<p style='text-align:center;'>로그인 후 임시 비밀번호를 새로운 비밀번호로 재설정해 주세요.</p>";
    }
}
