package Freeing.user_service.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AIConfig {

    @Bean
    public ChatClient chatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem("You are an AI designed to analyze survey responses related to stress levels. Based on the user's overall stress score and their general responses, provide personalized feedback and practical strategies to help manage and reduce stress effectively.")
                .build();
    }
}
