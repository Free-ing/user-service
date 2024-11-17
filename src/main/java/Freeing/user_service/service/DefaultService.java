package Freeing.user_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
public class DefaultService {

    private final WebClient sleepServiceClient;
    private final WebClient hobbyServiceClient;
    private final WebClient spiritServiceClient;
    private final WebClient exerciseServiceClient;

    @Autowired
    public DefaultService(WebClient.Builder webClientBuilder) {
        // 각 마이크로서비스의 base URL을 설정
        this.sleepServiceClient = webClientBuilder.baseUrl("lb://SLEEP-SERVICE").build();
        this.hobbyServiceClient = webClientBuilder.baseUrl("lb://HOBBY-SERVICE").build();
        this.spiritServiceClient = webClientBuilder.baseUrl("lb://SPIRIT-SERVICE").build();
        this.exerciseServiceClient = webClientBuilder.baseUrl("lb://EXERCISE-SERVICE").build();
    }

    public Mono<String> addDefaultSleepRoutine(Long userId) {
        log.info("sleep-service에 기본 기능 생성 요청");

        return sleepServiceClient.post()
                .uri("/sleep-service/routine/add/default/{userId}", userId)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(response -> log.info("sleep-service 응답: {}", response))
                .doOnError(e -> log.error("sleep-service 호출 중 오류 발생: ", e))
                .onErrorResume(e -> {
                    log.error("sleep-service 호출 실패로 기본 응답 반환", e);
                    return Mono.just("sleep-service 호출 실패");
                });
    }

    public Mono<String> addDefaultHobbyRoutine(Long userId) {
        log.info("hobby-service에 기본 기능 생성 요청");

        return hobbyServiceClient.post()
                .uri("/hobby-service/default-routine/{userId}", userId)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(response -> log.info("hobby-service 응답: {}", response))
                .doOnError(e -> log.error("hobby-service 호출 중 오류 발생: ", e))
                .onErrorResume(e -> {
                    log.error("hobby-service 호출 실패로 기본 응답 반환", e);
                    return Mono.just("hobby-service 호출 실패");
                });
    }

    public Mono<String> addDefaultSpiritRoutine(Long userId) {
        log.info("spirit-service에 기본 기능 생성 요청");

        return spiritServiceClient.post()
                .uri("/spirit-service/default-routine/{userId}", userId)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(response -> log.info("spirit-service 응답: {}", response))
                .doOnError(e -> log.error("spirit-service 호출 중 오류 발생: ", e))
                .onErrorResume(e -> {
                    log.error("spirit-service 호출 실패로 기본 응답 반환", e);
                    return Mono.just("spirit-service 호출 실패");
                });
    }

    public Mono<String> addDefaultExerciseRoutine(Long userId) {
        log.info("exercise-service에 기본 기능 생성 요청");

        return exerciseServiceClient.post()
                .uri("/exercise-service/default-routine/{userId}", userId)
                .retrieve()
                .bodyToMono(String.class)
                .doOnNext(response -> log.info("exercise-service 응답: {}", response))
                .doOnError(e -> log.error("exercise-service 호출 중 오류 발생: ", e))
                .onErrorResume(e -> {
                    log.error("exercise-service 호출 실패로 기본 응답 반환", e);
                    return Mono.just("exercise-service 호출 실패");
                });
    }
}
