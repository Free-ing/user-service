package Freeing.user_service.service;

import Freeing.user_service.dto.SurveyComparisonDto;
import Freeing.user_service.dto.SurveyResponseDto;
import Freeing.user_service.dto.SurveySummaryDto;
import Freeing.user_service.error.BadRequestException;
import Freeing.user_service.error.NotFoundException;
import Freeing.user_service.repository.SurveyResult;
import Freeing.user_service.repository.SurveyResultRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class SurveyServiceImpl implements SurveyService {
    private final SurveyResultRepository surveyResultRepository;
    private final ChatClient chatClient;
    private final TranslationService translationService; // TranslationService 주입

    @Override
    public SurveyResult saveSurveyResult(Long userId, List<SurveyResponseDto> responses) {
        // 오늘 생성된 설문 결과가 있는지 확인
        LocalDate today = LocalDate.now();
        boolean hasTodayResult = surveyResultRepository.existsByUserIdAndCreatedDate(userId, today);

        if (hasTodayResult) {
            throw new BadRequestException("이미 오늘의 피드백이 생성되었습니다. 새로운 피드백은 내일 생성할 수 있습니다.");
        }

        // SurveyResult 엔터티 생성
        SurveyResult result = new SurveyResult();
        result.setUserId(userId);

        int totalScore = 0;
        for (SurveyResponseDto dto : responses) {
            int answer = dto.getAnswer();
            totalScore += answer;

            // 각 문항에 응답 값을 매핑
            switch (dto.getQuestionNumber()) {
                case 1 -> result.setQuestion1(answer);
                case 2 -> result.setQuestion2(answer);
                case 3 -> result.setQuestion3(answer);
                case 4 -> result.setQuestion4(answer);
                case 5 -> result.setQuestion5(answer);
                case 6 -> result.setQuestion6(answer);
                case 7 -> result.setQuestion7(answer);
                case 8 -> result.setQuestion8(answer);
                case 9 -> result.setQuestion9(answer);
                case 10 -> result.setQuestion10(answer);
                case 11 -> result.setQuestion11(answer);
                default -> throw new IllegalArgumentException("잘못된 문항 번호입니다: " + dto.getQuestionNumber());
            }
        }

        // 총점과 스트레스 수준 설정
        result.setTotalScore(totalScore);
        result.setStressLevel(determineStressLevel(totalScore));

        mapResponsesToResult(result, responses);
        // AI 피드백 생성
        String aiFeedback = generateAIFeedback(result);
        String refinedFeedback = refineKoreanFeedback(aiFeedback);  // 자연스럽게 다듬기
        refinedFeedback = refinedFeedback.replace("*", "").replace("#", "");

        log.info("더 자연스럽게 수정된 버전: "+refinedFeedback);

        result.setAiFeedback(refinedFeedback);

        result.setCreatedDate(LocalDate.now());

        return surveyResultRepository.save(result);
    }

    @Override
    public List<SurveySummaryDto> getSurveyResultsByUserId(Long userId) {
        return surveyResultRepository.findByUserId(userId).stream()
                .map(result -> new SurveySummaryDto(
                        result.getSurveyId(),
                        result.getTotalScore(),
                        result.getStressLevel(),
                        result.getCreatedDate()
                ))
                .collect(Collectors.toList());
    }

    // 월별 설문조사 결과를 가져오는 메서드
    @Override
    public List<SurveySummaryDto> getSurveyResultsByUserIdAndMonth(Long userId, int year, int month) {
        List<SurveyResult> results = surveyResultRepository.findByUserIdAndYearAndMonth(userId, year, month);

        return results.stream()
                .map(result -> new SurveySummaryDto(
                        result.getSurveyId(),
                        result.getTotalScore(),
                        result.getStressLevel(),
                        result.getCreatedDate()
                ))
                .collect(Collectors.toList());
    }

    @Override
    public SurveyResult getSurveyResultDetail(Long userId, Long surveyId) {
        return surveyResultRepository.findByUserIdAndSurveyId(userId, surveyId)
                .orElseThrow(()->new NotFoundException("해당되는 것이 없습니다."));
    }

    public SurveyComparisonDto getRecentSurveyComparison(Long userId) {
        // 최근 두 개의 데이터를 조회
        List<SurveyResult> recentResults = surveyResultRepository.findTop2ByUserIdOrderByCreatedDateDesc(userId);

        // 데이터가 없는 경우 처리
        if (recentResults.isEmpty()) {
            throw new NotFoundException("해당 사용자의 데이터가 없습니다.");
        }

        // 데이터가 1개인 경우 처리
        if (recentResults.size() == 1) {
            SurveyResult recent = recentResults.get(0);
            return new SurveyComparisonDto(recent.getTotalScore(), recent.getStressLevel(), null, recent.getCreatedDate());
        }

        // 데이터가 2개 이상인 경우 처리
        SurveyResult recent = recentResults.get(0);
        SurveyResult previous = recentResults.get(1);
        int scoreDifference = recent.getTotalScore() - previous.getTotalScore();

        return new SurveyComparisonDto(recent.getTotalScore(), recent.getStressLevel(), scoreDifference, recent.getCreatedDate());
    }

    private String determineStressLevel(int totalScore) {
        if (totalScore <= 10) return "낮음";
        else if (totalScore <= 20) return "중간";
        else return "높음";
    }

    private void mapResponsesToResult(SurveyResult result, List<SurveyResponseDto> responses) {
        responses.forEach(response -> {
            switch (response.getQuestionNumber()) {
                case 1 -> result.setQuestion1(response.getAnswer());
                case 2 -> result.setQuestion2(response.getAnswer());
                case 3 -> result.setQuestion3(response.getAnswer());
                case 4 -> result.setQuestion4(response.getAnswer());
                case 5 -> result.setQuestion5(response.getAnswer());
                case 6 -> result.setQuestion6(response.getAnswer());
                case 7 -> result.setQuestion7(response.getAnswer());
                case 8 -> result.setQuestion8(response.getAnswer());
                case 9 -> result.setQuestion9(response.getAnswer());
                case 10 -> result.setQuestion10(response.getAnswer());
                case 11 -> result.setQuestion11(response.getAnswer());
                default -> throw new IllegalArgumentException("잘못된 문항 번호입니다: " + response.getQuestionNumber());
            }
        });
    }


    private String generateAIFeedback(SurveyResult surveyResult) {
        String prompt = createPrompt(surveyResult);
        String englishFeedback = chatClient.prompt(prompt).call().content();
        String koreanFeedback = translationService.translateToKorean(englishFeedback);
        koreanFeedback = koreanFeedback.replace("*", "").replace("#", "");
        return koreanFeedback;
    }
    private String refineKoreanFeedback(String translatedFeedback) {
        String refinementPrompt = String.format(
                "Please review the following Korean feedback carefully and adjust it to make the language sound natural, warm, and friendly, as if speaking to a friend. Please avoid using symbols like '*' and '#'. For emphasis, feel free to use emojis or adjust the tone instead. Korean feedback: \"%s\"", translatedFeedback);

        return chatClient.prompt(refinementPrompt).call().content();
    }


    private String createPrompt(SurveyResult surveyResult) {
        StringBuilder prompt = new StringBuilder();
        // 응답 의미 설명 추가
        prompt.append("This survey is designed to measure the user's stress level. The user is asked to rate how often they have experienced each symptom over the past two weeks.\n");
        prompt.append("Please interpret the response values as follows:\n");
        prompt.append("0: Not at all (Never experienced)\n");
        prompt.append("1: Rarely (Experienced at least 2 days)\n");
        prompt.append("2: Often (Experienced for at least 1 week)\n");
        prompt.append("3: Almost daily (Experienced for almost 2 weeks)\n\n");

        prompt.append("Based on the following survey results, provide detailed feedback and suggestions to help reduce stress:\n");
        prompt.append("Total Score: ").append(surveyResult.getTotalScore()).append("\n");
        prompt.append("Stress Level: ").append(surveyResult.getStressLevel()).append("\n");
        prompt.append("Responses:\n");
        prompt.append("Q1: Do you often feel stressed? Answer=").append(surveyResult.getQuestion1()).append("\n");
        prompt.append("Q2: Do you find it difficult to adapt to changes? Answer=").append(surveyResult.getQuestion2()).append("\n");
        prompt.append("Q3: Do you lack confidence in handling problems directly? Answer=").append(surveyResult.getQuestion3()).append("\n");
        prompt.append("Q4: Do you often experience headaches? Answer=").append(surveyResult.getQuestion4()).append("\n");
        prompt.append("Q5: Do you often feel dizzy? Answer=").append(surveyResult.getQuestion5()).append("\n");
        prompt.append("Q6: Do you experience digestive issues? Answer=").append(surveyResult.getQuestion6()).append("\n");
        prompt.append("Q7: Do you feel chest tightness? Answer=").append(surveyResult.getQuestion7()).append("\n");
        prompt.append("Q8: Do you feel anxious or nervous frequently? Answer=").append(surveyResult.getQuestion8()).append("\n");
        prompt.append("Q9: Do you get angry easily? Answer=").append(surveyResult.getQuestion9()).append("\n");
        prompt.append("Q10: Do you get irritated easily? Answer=").append(surveyResult.getQuestion10()).append("\n");
        prompt.append("Q11: Do you often eat to cope with stress? Answer=").append(surveyResult.getQuestion11()).append("\n");
        prompt.append("Based on these answers, please provide thoughtful and practical feedback that could help the user manage their stress better. ");
        prompt.append("The feedback should sound as though '후링이' is gently talking to the user, offering warm and supportive advice with encouragement.\n");
        prompt.append("Please avoid using symbols like '*' and '#'. For emphasis, feel free to use emojis or adjust the tone instead.\\n\\n");
        return prompt.toString();
    }


}