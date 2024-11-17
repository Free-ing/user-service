package Freeing.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface SurveyResultRepository extends JpaRepository<SurveyResult, Long> {
    List<SurveyResult> findByUserId(Long userId);
    Optional<SurveyResult> findByUserIdAndSurveyId(Long userId, Long surveyId);

    // 사용자 ID와 날짜로 오늘 결과가 있는지 확인
    boolean existsByUserIdAndCreatedDate(Long userId, LocalDate createdDate);

    // 사용자 ID로 최근 2개의 데이터를 생성일 기준 내림차순으로 조회
    List<SurveyResult> findTop2ByUserIdOrderByCreatedDateDesc(Long userId);

    @Query("SELECT s FROM SurveyResult s WHERE s.userId = :userId " +
            "AND EXTRACT(YEAR FROM s.createdDate) = :year " +
            "AND EXTRACT(MONTH FROM s.createdDate) = :month")
    List<SurveyResult> findByUserIdAndYearAndMonth(
            @Param("userId") Long userId,
            @Param("year") int year,
            @Param("month") int month);


}
