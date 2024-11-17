package Freeing.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SurveyComparisonDto {
    private Integer recentTotalScore; // 가장 최근 데이터의 총점
    private String recentStressLevel; // 가장 최근 데이터의 스트레스 수준
    private Integer scoreDifference;  // 최근 데이터와 그 전 데이터의 점수 차이 (데이터가 하나일 경우 null)
    private LocalDate createdDate;
}
