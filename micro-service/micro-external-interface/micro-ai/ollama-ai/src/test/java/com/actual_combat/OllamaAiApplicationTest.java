package com.actual_combat;

import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

/**
 * @Author yan
 * @Date 2025/5/15 14:19:50
 * @Description
 */
@Slf4j
@SpringBootTest
public class OllamaAiApplicationTest {
    @Test
    void test(){
        ChatClient chatClient = SpringUtil.getBean(ChatClient.class);
        String content = chatClient.prompt().user("你是谁？").call().content();
        log.info("content:{}",content);
        //Flux<String> content = chatClient.prompt().user("你是谁？").stream().content();
        //// 订阅 Flux 用于调试日志
        //content.doOnNext(chunk -> log.warn("Stream chunk: {}", chunk))
        //        .doOnError(error -> log.error("Stream error: {}", error.getMessage()))
        //        .doOnComplete(() -> log.warn("Stream completed"))
        //        .subscribe(); // 确保订阅以触发流

    }
}
