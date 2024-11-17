package Freeing.user_service.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String toEmail, String verificationCode) {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        String subject = "이메일 인증";
        String body = "인증 코드: " + verificationCode;

        try {
            helper.setTo(toEmail);
            helper.setSubject(subject);
            helper.setText(body, true);  // HTML 이메일의 경우 true
            helper.setFrom("freeing3355@naver.com");
        } catch (MessagingException e) {
            e.printStackTrace();
            throw new RuntimeException("이메일 전송 실패");
        }

        mailSender.send(mimeMessage);
    }
}
