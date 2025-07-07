package com.actual.combat.ai.langchain4j.edu.volunteer.controller;

import com.actual.combat.ai.langchain4j.edu.volunteer.ai_service.ConsultantService;
import jakarta.annotation.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

/**
 * @Author yan
 * @Date 2025/6/24 15:18:25
 * @Description
 */
@RequestMapping("/chat")
@RestController
public class ChatController {
    @Resource
    private ConsultantService consultantService;
    //@Resource
    //private StreamingChatLanguageModel streamingChatModel;
    @GetMapping(value = "",produces = {"text/html;charset=UTF-8"})
    public Flux<String> chat(@RequestParam("memoryId") String memoryId,@RequestParam("message") String prompt) {
        //return chatLanguageModel.chat(UserMessage.from(prompt).toString());
        return consultantService.chat(memoryId,prompt);
    }
}
