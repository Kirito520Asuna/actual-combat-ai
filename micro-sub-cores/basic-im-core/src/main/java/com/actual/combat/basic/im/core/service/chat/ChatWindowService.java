package com.actual.combat.basic.im.core.service.chat;


import com.actual.combat.basic.core.enums.im.ChatType;
import com.actual.combat.im.domain.chat.ChatWindow;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Author minimalism
 * @Date 2023/8/10 0010 14:22
 * @Description
 */
public interface ChatWindowService extends IService<ChatWindow> {


        ChatWindow getChatWindow(Long uid, Long tid, ChatType chatType);
    }
