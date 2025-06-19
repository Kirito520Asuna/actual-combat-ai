package com.actual.combat.basic.im.core.service.chat;



import com.actual.combat.im.domain.chat.ChatMessage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Author minimalism
 * @Date 2023/8/10 0010 14:22
 * @Description
 */
public interface ChatMessageService extends IService<ChatMessage> {


    /**
     * 聊天窗口聊天记录
     *
     * @param chatId
     * @param userId
     * @return
     */
    List<ChatMessage> getList(Long chatId, Long userId);

    /**
     * 发送消息
     * @param chatMessage
     */
    void sendMessage(ChatMessage chatMessage);
}
