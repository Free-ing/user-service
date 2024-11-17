package Freeing.user_service.config;

import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    @LoadBalanced // Eureka와 같은 서비스 디스커버리를 사용하는 경우 필요합니다.
    public WebClient.Builder webClientBuilder() {
        return WebClient.builder();
    }

    @Bean
    public WebClient externalWebClient() {  // 외부 API 호출에 사용될 WebClient
        return WebClient.builder().build();
    }
}
