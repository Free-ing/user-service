package Freeing.user_service.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Service
@Slf4j
public class TranslationService {
    private final WebClient webClient;
    private final String deeplApiKey;

    @Autowired
    public TranslationService(@Qualifier("externalWebClient") WebClient webClient,
                              @Value("${deepl.api-key}") String deeplApiKey) {
        this.webClient = webClient;
        this.deeplApiKey = deeplApiKey;
    }

    public String translateToKorean(String text) {
        try {
            // WebClient를 사용해 API 요청
            String encodedResponse = webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .scheme("https")
                            .host("api-free.deepl.com")
                            .path("/v2/translate")
                            .queryParam("auth_key", deeplApiKey)
                            .queryParam("text", text)
                            .queryParam("target_lang", "KO")
                            .build())
                    .retrieve()
                    .bodyToMono(String.class)
                    .map(this::parseTranslation)
                    .block();

            // URL 디코딩 수행
            String decodedResponse = URLDecoder.decode(encodedResponse, StandardCharsets.UTF_8);
            log.info("번역된 피드백(디코딩됨): " + decodedResponse); // 디코딩된 결과 확인
            return decodedResponse;

        } catch (WebClientResponseException e) {
            log.error("DeepL API 응답 오류: " + e.getMessage());
            return "번역 오류";
        } catch (Exception e) {
            log.error("예외 발생: ", e);
            return "번역 오류";
        }
    }

    private String parseTranslation(String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode root = objectMapper.readTree(response);
            return root.path("translations").get(0).path("text").asText();
        } catch (Exception e) {
            log.error("Parsing error: " + e.getMessage());
            return "번역 오류";
        }
    }
}
