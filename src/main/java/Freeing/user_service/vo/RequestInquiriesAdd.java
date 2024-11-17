package Freeing.user_service.vo;

import Freeing.user_service.dto.InquiriesCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RequestInquiriesAdd {
    @NotNull(message = "문의 사항 종류는 필수 입력 항목입니다")
    private InquiriesCategory category;
    @NotBlank(message = "문의 사항 제목은 필수 입력 항목입니다")
    private String inquiriesTitle;
    @NotBlank(message = "문의 사항 내용은 필수 입력 항목입니다")
    private String content;


}
