package Freeing.user_service.vo;

import Freeing.user_service.dto.AnnouncementCategory;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestUpdateAnnouncement {
    @NotNull(message = "공지사항 식별 번호는 필수 입력 사항입니다.")
    private Long announcementId;

    @NotBlank(message = "공지사항 제목은 필수 입력 항목입니다")
    private String title;

    @NotBlank(message = "공지사항 내용은 필수 입력 항목입니다")
    private String content;

    @NotNull(message = "공지사항 종류는 필수 입력 항목입니다")
    private AnnouncementCategory category;
}
