package com.actual.combat.ai.langchain4j.edu.volunteer.ai_service;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;
import reactor.core.publisher.Flux;

/**
 * @Author yan
 * @Date 2025/6/24 12:42:18
 * @Description
 */
 //集成mybatis plus 报错疑似langchain4j扫包冲突导致bean设置类型不对
@AiService(
        wiringMode = AiServiceWiringMode.EXPLICIT,//手动装配
        chatModel = "openAiChatModel",//配置 指定模型
        streamingChatModel = "openAiStreamingChatModel",
        //chatMemory = "chatMemory",//配置会话记忆对象
        chatMemoryProvider = "chatMemoryProvider",//配置会话记忆对象提供者
        contentRetriever = "contentRetriever",//配置向量数据库检索对象
        tools = {"reservationTool"}//配置工具
)
public interface ConsultantService {
    //String chat(String message);
    //@SystemMessage("你是可爱的助手,名字叫小甜,由天天开发公司开发,请以该身份回复用户,并以md语法回复(非询问身份不要介绍自己,请直接回复用户的问题,请用中文回复用户,剔除结语)")
    @SystemMessage(fromResource = "system.txt")
    Flux<String> chat(@MemoryId String memoryId, @UserMessage String message);

}
