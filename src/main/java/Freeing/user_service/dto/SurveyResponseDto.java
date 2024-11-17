package Freeing.user_service.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class SurveyResponseDto {

    @NotNull(message = "문항 번호는 필수입니다.")
    @Min(value = 1, message = "문항 번호는 1 이상이어야 합니다.")
    @Max(value = 11, message = "문항 번호는 11 이하이어야 합니다.")
    private Integer questionNumber; // 문항 번호

    @NotNull(message = "답변 값은 필수입니다.")
    @Min(value = 0, message = "답변 값은 0 이상이어야 합니다.")
    @Max(value = 3, message = "답변 값은 3 이하이어야 합니다.")
    private Integer answer; // 답변 (0~3 점수)
}
