package Freeing.user_service.dto;

import lombok.Data;

@Data
public class InquiriesDto {
    private InquiriesCategory category;
    private String inquiriesTitle;
    private String content;
    private Long userId;
}
