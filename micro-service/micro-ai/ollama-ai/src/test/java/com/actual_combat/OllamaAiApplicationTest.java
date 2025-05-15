package com.actual_combat;

import cn.hutool.extra.spring.SpringUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.boot.test.context.SpringBootTest;

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
    }
}
