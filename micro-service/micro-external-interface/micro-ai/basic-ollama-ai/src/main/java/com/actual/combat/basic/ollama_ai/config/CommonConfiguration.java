package com.actual.combat.basic.ollama_ai.config;

import com.actual.combat.basic.ollama_ai.abs.AbstractAiConfiguration;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author yan
 * @Date 2025/5/15 14:14:39
 * @Description
 */
@Configuration
public  class CommonConfiguration implements AbstractAiConfiguration {
    @Bean
    public ChatClient chatClient(OllamaChatModel model) {
        return buildDefaultChatClient((ChatClient) model);
    }
}
