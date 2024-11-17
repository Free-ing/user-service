package Freeing.user_service.vo;

import Freeing.user_service.dto.AnnouncementCategory;
import Freeing.user_service.repository.AnnouncementEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestCreateAnnouncement {
    @NotBlank(message = "공지사항 제목은 필수 입력 항목입니다")
    private String title;
    @NotBlank(message = "공지사항 내용은 필수 입력 항목입니다")
    private String content;
    @NotNull(message = "공지사항 종류는 필수 입력 항목입니다")
    private AnnouncementCategory category;

    public AnnouncementEntity toEntity() {
        return AnnouncementEntity.builder()
                .title(this.title)
                .content(this.content)
                .createdDate(LocalDateTime.now())
                .category(this.category)
                .build();
    }
}
