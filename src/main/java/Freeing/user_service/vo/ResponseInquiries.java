package Freeing.user_service.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResponseInquiries {
    private Long inquiriesId;
    private String inquiriesTitle;
    private String content;
    private LocalDateTime createdAt;

    private ResponseAnswer answer;  // 답변 정보 포함
}
