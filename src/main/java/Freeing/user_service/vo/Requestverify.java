package Freeing.user_service.vo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.bind.annotation.RequestParam;

@Data
public class Requestverify {

    @Email(message = "올바른 이메일 형식이어야 합니다")
    @NotBlank(message = "이메일은 필수 입력 항목입니다")
    private String email;

    @NotBlank(message = "인증번호는 필수 입력 항목입니다")
    @Size(min = 6,max =6, message = "인증번호는 6자리입니다.")
    private String code;
}
