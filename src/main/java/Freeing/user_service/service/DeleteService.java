package Freeing.user_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@Transactional
public class DeleteService {

    private final WebClient sleepServiceClient;
    private final WebClient hobbyServiceClient;
    private final WebClient spiritServiceClient;
    private final WebClient exerciseServiceClient;

    @Autowired
    public DeleteService(WebClient.Builder webClientBuilder) {
        // 각 서비스별 WebClient 인스턴스 설정
        this.sleepServiceClient = webClientBuilder.baseUrl("lb://SLEEP-SERVICE").build();
        this.hobbyServiceClient = webClientBuilder.baseUrl("lb://HOBBY-SERVICE").build();
        this.spiritServiceClient = webClientBuilder.baseUrl("lb://SPIRIT-SERVICE").build();
        this.exerciseServiceClient = webClientBuilder.baseUrl("lb://EXERCISE-SERVICE").build();
    }

    public Mono<String> deleteAll(Long userId) {
        log.info("회원 삭제 요청 시작: userId = {}", userId);

        // 각 서비스에 데이터 삭제 요청 보내기
        deleteSleepData(userId).subscribe(response -> log.info("수면 데이터 삭제 응답: {}", response));
        deleteHobbyData(userId).subscribe(response -> log.info("취미 데이터 삭제 응답: {}", response));
        deleteSpiritData(userId).subscribe(response -> log.info("마음 데이터 삭제 응답: {}", response));
        deleteExerciseData(userId).subscribe(response -> log.info("운동 데이터 삭제 응답: {}", response));

        return Mono.just("회원의 모든 데이터 삭제 요청 완료");
    }

    public Mono<String> resetAll(Long userId) {
        log.info("회원 삭제 요청 시작: userId = {}", userId);

        // 각 서비스에 데이터 삭제 요청 보내기
        resetSleepData(userId).subscribe(response -> log.info("수면 데이터 삭제 응답: {}", response));
        deleteHobbyData(userId).subscribe(response -> log.info("취미 데이터 삭제 응답: {}", response));
        deleteSpiritData(userId).subscribe(response -> log.info("마음 데이터 삭제 응답: {}", response));
        deleteExerciseData(userId).subscribe(response -> log.info("운동 데이터 삭제 응답: {}", response));

        return Mono.just("회원의 모든 데이터 삭제 요청 완료");
    }
    private Mono<String> resetSleepData(Long userId) {
        return sleepServiceClient.delete()
                .uri("/sleep-service/reset/all/{userId}", userId)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(e -> log.error("수면 데이터 삭제 중 오류 발생: ", e))
                .onErrorResume(e -> {
                    log.error("수면 데이터 삭제 호출 실패", e);
                    return Mono.just("수면 데이터 삭제 실패");
                });
    }


    private Mono<String> deleteSleepData(Long userId) {
        return sleepServiceClient.delete()
                .uri("/sleep-service/delete/all/{userId}", userId)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(e -> log.error("수면 데이터 삭제 중 오류 발생: ", e))
                .onErrorResume(e -> {
                    log.error("수면 데이터 삭제 호출 실패", e);
                    return Mono.just("수면 데이터 삭제 실패");
                });
    }

    private Mono<String> deleteHobbyData(Long userId) {
        return hobbyServiceClient.delete()
                .uri("/hobby-service/users/{userId}", userId)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(e -> log.error("취미 데이터 삭제 중 오류 발생: ", e))
                .onErrorResume(e -> {
                    log.error("취미 데이터 삭제 호출 실패", e);
                    return Mono.just("취미 데이터 삭제 실패");
                });
    }

    private Mono<String> deleteSpiritData(Long userId) {
        return spiritServiceClient.delete()
                .uri("/spirit-service/users/{userId}", userId)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(e -> log.error("마음 데이터 삭제 중 오류 발생: ", e))
                .onErrorResume(e -> {
                    log.error("마음 데이터 삭제 호출 실패", e);
                    return Mono.just("마음 데이터 삭제 실패");
                });
    }

    private Mono<String> deleteExerciseData(Long userId) {
        return exerciseServiceClient.delete()
                .uri("/exercise-service/users/{userId}", userId)
                .retrieve()
                .bodyToMono(String.class)
                .doOnError(e -> log.error("운동 데이터 삭제 중 오류 발생: ", e))
                .onErrorResume(e -> {
                    log.error("운동 데이터 삭제 호출 실패", e);
                    return Mono.just("운동 데이터 삭제 실패");
                });
    }
}
