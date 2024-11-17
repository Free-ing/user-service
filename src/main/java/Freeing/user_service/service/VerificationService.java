package Freeing.user_service.service;

import Freeing.user_service.repository.EmailVerification;
import Freeing.user_service.repository.EmailVerificationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Random;

@Service
@Transactional
public class VerificationService {
    private final EmailVerificationRepository emailVerificationRepository;

    private final Random random = new Random();

    @Autowired
    public VerificationService(EmailVerificationRepository emailVerificationRepository) {
        this.emailVerificationRepository = emailVerificationRepository;
    }

    public String generateVerificationCode() {
        int code = 100000 + random.nextInt(900000);  // 6자리 난수 생성
        return String.valueOf(code);
    }
    public void saveVerificationCode(String email, String verificationCode) {
        // 기존 이메일로 저장된 인증 코드가 있으면 삭제
        emailVerificationRepository.deleteByEmail(email);

        // 새 인증 코드 저장
        EmailVerification verification = new EmailVerification();
        verification.setEmail(email);
        verification.setVerificationCode(verificationCode);
        verification.setCreatedAt(LocalDateTime.now());
        verification.setExpiresAt(LocalDateTime.now().plus(10, ChronoUnit.MINUTES));

        emailVerificationRepository.save(verification);
    }
}
