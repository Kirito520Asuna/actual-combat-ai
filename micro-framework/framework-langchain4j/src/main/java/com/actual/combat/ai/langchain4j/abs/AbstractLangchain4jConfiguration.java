package com.actual.combat.ai.langchain4j.abs;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;

/**
 * @Author yan
 * @Date 2025/6/24 22:27:23
 * @Description
 */
public interface AbstractLangchain4jConfiguration {
    //构建会话对象
    default ChatMemory buildDefaultChatMemory() {
        MessageWindowChatMemory build = MessageWindowChatMemory.builder()
                .maxMessages(20)
                .build();
        return build;
    }
}
