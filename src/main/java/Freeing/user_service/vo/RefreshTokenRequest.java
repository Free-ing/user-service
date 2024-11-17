package Freeing.user_service.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RefreshTokenRequest {
    @NotBlank(message = "RefreshToken은 필수 입력 항목입니다")
    private String refreshToken;
}
