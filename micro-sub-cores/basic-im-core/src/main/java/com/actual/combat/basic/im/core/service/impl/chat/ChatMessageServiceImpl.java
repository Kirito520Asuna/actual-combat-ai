package com.actual.combat.basic.im.core.service.impl.chat;

import com.actual.combat.basic.core.constant.datasource.DataSourceName;
import com.actual.combat.basic.core.enums.im.MessageType;
import com.actual.combat.basic.core.vo.user.UserVo;
import com.actual.combat.im.domain.chat.ChatMessage;
import com.actual.combat.im.domain.im.Message;
import com.actual.combat.im.mapper.chat.ChatMessageMapper;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import java.time.LocalDateTime;

import com.actual.combat.basic.im.core.service.im.MessageService;
import com.actual.combat.basic.im.core.service.chat.ChatMessageService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

/**
 * @Author minimalism
 * @Date 2023/8/10 0010 14:22
 * @Description
 */
@Service @DS(DataSourceName.im)
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements ChatMessageService {

    @Resource
    private ChatMessageMapper dao;
    @Resource
    private MessageService messageService;

    @Override
    public List<ChatMessage> getList(Long chatId, Long userId) {
        List<ChatMessage> list = dao.getList(chatId);
        list.stream().forEach(chatMessage->{
            //判断发送消息的是否是自己
            boolean isSelf = chatMessage.getSendUserId().equals(userId);
            UserVo sendUser = chatMessage.getSendUser();
            sendUser.setIsSelf(isSelf);
        });
        return list;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void sendMessage(ChatMessage chatMessage) {
        Long sendUserId = chatMessage.getSendUserId();
        String content = chatMessage.getContent();
        MessageType type = chatMessage.getType();

        LocalDateTime now = LocalDateTime.now();
        Message message = new Message(null,sendUserId,null,type,content, now);
        messageService.saveOrUpdate(message);
        Long messageId = message.getId();
        chatMessage.setMessageId(messageId);
        saveOrUpdate(chatMessage);
    }
}
