package Freeing.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;


@Data
@AllArgsConstructor
public class SurveySummaryDto {
    private Long surveyId;
    private int totalScore;
    private String stressLevel;
    private LocalDate createdDate;
}
