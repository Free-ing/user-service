package Freeing.user_service.service;


import Freeing.user_service.dto.SurveyComparisonDto;
import Freeing.user_service.dto.SurveyResponseDto;
import Freeing.user_service.dto.SurveySummaryDto;
import Freeing.user_service.repository.SurveyResult;

import java.util.List;

public interface SurveyService {
    SurveyResult saveSurveyResult(Long userId, List<SurveyResponseDto> responses);
    List<SurveySummaryDto> getSurveyResultsByUserId(Long userId);

    SurveyResult getSurveyResultDetail(Long userId, Long surveyId);
    SurveyComparisonDto getRecentSurveyComparison(Long userId);

    List<SurveySummaryDto> getSurveyResultsByUserIdAndMonth(Long userId, int year, int month);

    }
