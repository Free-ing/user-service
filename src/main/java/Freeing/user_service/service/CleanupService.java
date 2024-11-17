package Freeing.user_service.service;

import Freeing.user_service.repository.EmailVerificationRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class CleanupService {

    private final EmailVerificationRepository emailVerificationRepository;

    public CleanupService(EmailVerificationRepository emailVerificationRepository) {
        this.emailVerificationRepository = emailVerificationRepository;
    }

    @Scheduled(cron = "0 0 3 * * ?")
    public void cleanupExpiredVerificationCodes() {
        LocalDateTime now = LocalDateTime.now();
        // 만료된 데이터 삭제
        emailVerificationRepository.deleteByExpiresAtBefore(now);
    }
}
