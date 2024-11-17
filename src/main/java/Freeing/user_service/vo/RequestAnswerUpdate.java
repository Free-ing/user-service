package Freeing.user_service.vo;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RequestAnswerUpdate {

    @NotBlank(message = "답변 내용은 필수 입력 항목입니다")
    private String content;
}
