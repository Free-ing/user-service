package Freeing.user_service.vo;

import Freeing.user_service.dto.FeedbackStatus;
import lombok.Data;

@Data
public class RequestUpdateStatus_FeedBack {
    private Long inquiriesId;
    private FeedbackStatus feedbackStatus;
}
