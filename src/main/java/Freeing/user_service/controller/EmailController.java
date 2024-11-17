    package Freeing.user_service.controller;

    import Freeing.user_service.repository.EmailVerification;
    import Freeing.user_service.repository.EmailVerificationRepository;
    import Freeing.user_service.service.EmailService;
    import Freeing.user_service.service.VerificationService;
    import Freeing.user_service.vo.Requestverify;
    import jakarta.validation.Valid;
    import lombok.extern.slf4j.Slf4j;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.http.HttpStatus;
    import org.springframework.http.ResponseEntity;
    import org.springframework.web.bind.annotation.*;

    import java.time.LocalDateTime;

    @RestController
    @RequestMapping("/user-service/email")
    @Slf4j
    public class EmailController {

        private final EmailService emailService;
        private final VerificationService verificationService;
        private final EmailVerificationRepository emailVerificationRepository;

        @Autowired
        public EmailController(EmailService emailService, VerificationService verificationService, EmailVerificationRepository emailVerificationRepository) {
            this.emailService = emailService;
            this.verificationService = verificationService;
            this.emailVerificationRepository = emailVerificationRepository;
        }

        @PostMapping("/send-verification")
        public String sendVerification(@RequestParam String email) {
            String verificationCode = verificationService.generateVerificationCode();
            log.info("인증코드 생성");
            emailService.sendVerificationEmail(email, verificationCode);
            log.info("인증코드 전송");


            // 인증 코드 및 이메일을 데이터베이스에 저장
            verificationService.saveVerificationCode(email, verificationCode);

            log.info("인증코드 저장");


            return "인증번호가 이메일로 전송되었습니다.";
        }

        @PostMapping("/verify")
        public ResponseEntity<String> verifyCode(@RequestBody@Valid Requestverify requestverify) {
            String email = requestverify.getEmail();
            String code = requestverify.getCode();
            // 데이터베이스에서 해당 이메일의 인증 코드 정보를 가져옵니다.
            EmailVerification verification = emailVerificationRepository.findByEmail(email);

            // 인증 기록이 없는 경우
            if (verification == null) {
                log.warn(email + "의 인증 기록을 찾을 수 없습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("인증 기록이 존재하지 않습니다. 다시 시도해 주세요.");
            }

            // 인증 코드의 만료 여부 확인
            if (verification.getExpiresAt().isBefore(LocalDateTime.now())) {
                log.warn(email + "의 인증 코드가 만료되었습니다. 만료 시간: " + verification.getExpiresAt());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("인증 코드가 만료되었습니다. 새 인증 코드를 요청해 주세요.");
            }

            // 입력된 인증 코드와 저장된 인증 코드를 비교
            if (verification.getVerificationCode().equals(code)) {
                // 인증 성공 처리
                log.info(email + "의 인증이 성공적으로 완료되었습니다.");
                return ResponseEntity.ok("인증이 성공적으로 완료되었습니다.");
            } else {
                // 잘못된 인증 코드인 경우
                log.warn(email + "의 인증 코드가 일치하지 않습니다. 입력된 코드: " + code);
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("인증 실패: 입력한 인증 코드가 올바르지 않습니다. 다시 시도해 주세요.");
            }
        }

    }
