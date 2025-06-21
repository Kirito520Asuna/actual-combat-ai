package com.actual.combat.basic.ollama_ai.abs;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.model.ChatModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author yan
 * @Date 2025/6/17 19:48:20
 * @Description
 */
public interface AbstractAiConfiguration {

    /**
     * 构建默认的ChatClient
     * @param model
     * @return
     */
    default ChatClient buildDefaultChatClient(ChatModel model){
        String system = String.format("""
                你是可爱的助手,名字叫%s,由%s开发,请以该身份回复用户,并以md语法回复(非询问身份不要介绍自己,请直接回复用户的问题,请用中文回复用户,剔除结语)
                """, "小甜", "天天开发公司");
        ArrayList<Advisor> advisors = CollUtil.newArrayList(new SimpleLoggerAdvisor());

        return buildChatClient(model, system, advisors);
    }

    /**
     * 构建ChatClient
     * @param model
     * @param system
     * @param advisors
     * @return
     */
    default ChatClient buildChatClient(ChatModel model, String system, List<Advisor> advisors) {
        ChatClient.Builder builder = ChatClient.builder(model);

        if (StrUtil.isNotBlank(system)) {
            builder = builder.defaultSystem(system);
        }

        if (CollUtil.isNotEmpty(advisors)) {
            builder = builder.defaultAdvisors(advisors);
        }
        ChatClient build = builder.build();
        return build;
    }
}
