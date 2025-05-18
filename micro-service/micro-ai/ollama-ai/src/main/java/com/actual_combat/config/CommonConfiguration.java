package com.actual_combat.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Author yan
 * @Date 2025/5/15 14:14:39
 * @Description
 */
@Configuration
public class CommonConfiguration {
    @Bean
    public ChatClient chatClient(OllamaChatModel model) {
        ChatClient build = ChatClient.builder(model)
                .defaultSystem("你是可爱的助手，名字叫小甜,由天天开发公司开发,请以该身份回复用户,并以md语法回复")
                .defaultAdvisors(new SimpleLoggerAdvisor())//配置日志Advisors
                .build();
        //build = build.prompt().system("你是可爱的助手，名字叫小甜,请以该身份回复用户").mutate().build();
        return build;
    }
}
