package com.actual.combat.basic.websocket.core.service.impl;

import cn.hutool.json.JSONUtil;
import com.actual.combat.basic.utils.object.ObjectUtils;
import com.actual.combat.basic.websocket.core.domain.Message;
import com.actual.combat.basic.websocket.core.enums.MessageType;
import com.actual.combat.basic.websocket.core.service.MessageService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * @Author yan
 * @Date 2025/2/5 13:57:02
 * @Description
 */
@Service
public class MessageServiceImpl implements MessageService {
    //@Resource
    //private ImClient imClient;
    //@Override
    //public void sendOfflineMessage(Message message) {
    //    //todo : 消息转发
    //    MessageService.super.sendOfflineMessage(message);
    //    String content = message.getContent();
    //    if(JSONUtil.isTypeJSON(content)){
    //        Map<String,Object> map = JSONUtil.toBean(content, Map.class);
    //    }
    //
    //    if (ObjectUtils.equals(message.getType(), MessageType.system)) {
    //
    //        String json = "{\n" +
    //                "            from: sendUserId,\n" +
    //                "            to: acceptUserId.value,\n" +
    //                "            chatId: chatId2,\n" +
    //                "            message: content,\n" +
    //                "            type: 2(未定义类型重构可重新定义)\n" +
    //                "          }";
    //    } else if (ObjectUtils.equals(message.getType(), MessageType.text)) {
    //
    //    }
    //
    //    OpenfeignChatMessage openfeignChatMessage = new OpenfeignChatMessage();
    //    openfeignChatMessage.setChatId(Long.parseLong("1"/*"chatId"*/));
    //    openfeignChatMessage.setSendUserId(Long.parseLong("1"/*"sendUserId"*/));
    //    openfeignChatMessage.setSendUser(new UserVo());
    //    openfeignChatMessage.setMessageId(null);
    //    openfeignChatMessage.setContent("message");
    //    openfeignChatMessage.setType(com.minimalism.base.enums.im.MessageType.valueOf("TXT"/*"type"*/));
    //    openfeignChatMessage.setTime(LocalDateTime.now());
    //    Result result = /*SpringUtil.getBean(ImClient.class)*/
    //    imClient.sendMessage(openfeignChatMessage);
    //    if (!result.validateOk()) {
    //        throw new GlobalCustomException(result.getMessage());
    //    }
    //}
}
