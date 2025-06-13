package com.actual_combat.controller;

import cn.hutool.extra.spring.SpringUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


/**
 * @Author yan
 * @Date 2025/5/16 18:39:06
 * @Description
 */
@Tag(name = "chat", description = "聊天")
@RequestMapping({"/api/chat","/jwt/chat","/chat"})
@RestController
public class ChatController {
    @Operation(summary = "聊天")
    @GetMapping(produces = {"text/html;charset=UTF-8"})
    public Flux<String> chat(@RequestParam("message") String message) {
        ChatClient chatClient = SpringUtil.getBean(ChatClient.class);
        return chatClient.prompt().user(message).stream().content();
    }
}
