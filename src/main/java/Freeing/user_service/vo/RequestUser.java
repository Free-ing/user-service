package Freeing.user_service.vo;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class RequestUser {

    @Email(message = "올바른 이메일 형식이어야 합니다")
    @NotBlank(message = "이메일은 필수 입력 항목입니다")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다")
    private String password;

    @NotBlank(message = "이름은 필수 입력 항목입니다")
    @Size(min = 2, message = "이름은 최소 2자 이상이어야 합니다")
    private String name;

    @NotNull(message = "역할은 필수 입력 항목입니다")  // 역할 필수 입력 사항 검사
    @Min(value = 0, message = "역할은 최소 0이어야 합니다")
    @Max(value = 1, message = "역할은 최대 1이어야 합니다")
    private Integer role;
}
