package Freeing.user_service.controller;

import Freeing.user_service.dto.SurveyComparisonDto;
import Freeing.user_service.dto.SurveyResponseDto;
import Freeing.user_service.dto.SurveySummaryDto;
import Freeing.user_service.error.BadRequestException;
import Freeing.user_service.error.NotFoundException;
import Freeing.user_service.repository.SurveyResult;
import Freeing.user_service.security.JwtTokenProvider;
import Freeing.user_service.service.SurveyService;
import Freeing.user_service.vo.SurveyRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.internal.bytebuddy.implementation.bytecode.Throw;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/user-service/stress-test")
@RequiredArgsConstructor
public class SurveyController {


    private final SurveyService surveyService;
    private final JwtTokenProvider jwtTokenProvider;
    private static final int REQUIRED_QUESTION_COUNT = 11; // 기대하는 문항 수

    @PostMapping("/results")
    public ResponseEntity<SurveyResult> submitSurveyResult(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @RequestBody @Valid SurveyRequest surveyRequest) {

        List<SurveyResponseDto> responses = surveyRequest.getResponses();

        // 문항 수 체크
        if (responses.size() != REQUIRED_QUESTION_COUNT) {
            throw new BadRequestException("모든 문항에 답을 해야 합니다.");
        }

        String token = authorizationHeader.substring(7);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        SurveyResult surveyResult = surveyService.saveSurveyResult(userId, responses);

        // aiFeedback URL 디코딩
        String decodedFeedback = decodeFeedback(surveyResult.getAiFeedback());
        surveyResult.setAiFeedback(decodedFeedback);  // 디코딩된 피드백을 설정

        return ResponseEntity.status(HttpStatus.CREATED).body(surveyResult);
    }

    @GetMapping("/results/list")
    public ResponseEntity<List<SurveySummaryDto>> getSurveyResultsByUserId(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader
            ) {

        String token = authorizationHeader.substring(7);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        List<SurveySummaryDto> surveyResults = surveyService.getSurveyResultsByUserId(userId);
        if (surveyResults.isEmpty()){
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(surveyResults);

        }
        return ResponseEntity.status(HttpStatus.OK).body(surveyResults);
    }

    @GetMapping("/results/list/monthly")
    public ResponseEntity<List<SurveySummaryDto>> getSurveyResultsByUserIdAndMonth(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @RequestParam int year,
            @RequestParam int month) {

        // JWT에서 사용자 ID 추출
        String token = authorizationHeader.substring(7);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        // 월별 데이터 조회
        List<SurveySummaryDto> surveyResults = surveyService.getSurveyResultsByUserIdAndMonth(userId, year, month);

        if (surveyResults.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(surveyResults);
        }

        return ResponseEntity.status(HttpStatus.OK).body(surveyResults);
    }


    @GetMapping("/results/{surveyId}")
    public ResponseEntity<SurveyResult> getSurveyResultDetail(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader,
            @PathVariable Long surveyId
    ){
        String token = authorizationHeader.substring(7);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        SurveyResult surveyResult = surveyService.getSurveyResultDetail(userId, surveyId);

        return ResponseEntity.status(HttpStatus.OK).body(surveyResult);
    }

    @GetMapping("/home")
    public ResponseEntity<SurveyComparisonDto> getRecentSurveyComparison(
            @RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {

        String token = authorizationHeader.substring(7);
        Long userId = jwtTokenProvider.getUserIdFromToken(token);

        SurveyComparisonDto response = surveyService.getRecentSurveyComparison(userId);
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


    public String decodeFeedback(String encodedFeedback) {
        try {
            return URLDecoder.decode(encodedFeedback, StandardCharsets.UTF_8.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return "디코딩 오류";
        }
    }
}
