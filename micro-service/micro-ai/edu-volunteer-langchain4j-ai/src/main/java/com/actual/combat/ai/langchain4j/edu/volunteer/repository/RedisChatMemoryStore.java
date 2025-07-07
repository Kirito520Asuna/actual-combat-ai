package com.actual.combat.ai.langchain4j.edu.volunteer.repository;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import jakarta.annotation.Resource;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;

/**
 * @Author yan
 * @Date 2025/6/26 22:28:43
 * @Description 会话记忆持久化 == > redis
 */
@Repository
public class RedisChatMemoryStore implements ChatMemoryStore {
    static String KEY_PREFIX = "ai:chat-memory:";
    @Resource
    private RedisTemplate redisTemplate;

    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        String json = (String) redisTemplate.opsForValue().get(KEY_PREFIX + memoryId);
        List<ChatMessage> chatMessages = ChatMessageDeserializer.messagesFromJson(json);
        return chatMessages;
    }

    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> list) {
        //1.list==>json
        String messagesToJson = ChatMessageSerializer.messagesToJson(list);
        //2.json==>redis
        redisTemplate.opsForValue().set(KEY_PREFIX + memoryId.toString(), messagesToJson, Duration.ofDays(1));
    }

    @Override
    public void deleteMessages(Object memoryId) {
        redisTemplate.delete(KEY_PREFIX + memoryId.toString());
    }
}
