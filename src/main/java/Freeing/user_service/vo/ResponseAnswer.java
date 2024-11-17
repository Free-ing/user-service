package Freeing.user_service.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResponseAnswer {
    private Long answerId;
    private Long inquiriesId;
    private String content;
    private LocalDateTime createdAt;
}