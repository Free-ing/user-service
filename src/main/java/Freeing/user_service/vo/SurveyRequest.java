package Freeing.user_service.vo;


import java.util.List;

import Freeing.user_service.dto.SurveyResponseDto;
import lombok.Data;

@Data
public class SurveyRequest {
    private List<SurveyResponseDto> responses;
}
