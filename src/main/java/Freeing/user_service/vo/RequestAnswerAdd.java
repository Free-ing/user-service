package Freeing.user_service.vo;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestAnswerAdd {
    @NotNull(message = "문의사항 ID는 필수 입력 항목입니다")
    private Long inquiriesId;

    @NotBlank(message = "답변 내용은 필수 입력 항목입니다")
    private String content;
}
