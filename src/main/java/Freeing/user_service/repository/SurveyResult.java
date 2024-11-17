package Freeing.user_service.repository;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SurveyResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long surveyId;

    private Long userId; // 사용자 ID

    // 각 문항별 점수를 저장하는 칼럼
    @Column(nullable = false)
    private int question1;
    @Column(nullable = false)
    private int question2;
    @Column(nullable = false)
    private int question3;
    @Column(nullable = false)
    private int question4;
    @Column(nullable = false)
    private int question5;
    @Column(nullable = false)
    private int question6;
    @Column(nullable = false)
    private int question7;
    @Column(nullable = false)
    private int question8;
    @Column(nullable = false)
    private int question9;
    @Column(nullable = false)
    private int question10;
    @Column(nullable = false)
    private int question11;

    @Column(nullable = false)
    private int totalScore; // 총점
    @Column(nullable = false)
    private String stressLevel; // 스트레스 수준 (예: 낮음, 중간, 높음)

    @Column(nullable = true)
    private LocalDate createdDate;
    @Lob
    private String aiFeedback;
}
