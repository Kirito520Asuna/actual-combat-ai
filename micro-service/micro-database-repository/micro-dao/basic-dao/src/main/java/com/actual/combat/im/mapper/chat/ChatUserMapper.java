package com.actual.combat.im.mapper.chat;


import com.actual.combat.basic.core.enums.im.ChatType;
import com.actual.combat.im.domain.chat.ChatUser;
import com.actual.combat.mp.abs.mapper.MpMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author yan
 * @Date 2023/8/10 0010 14:38
 * @Description
 */
@Mapper
public interface ChatUserMapper extends MpMapper<ChatUser> {

    boolean exitChatId(@Param("userId2") Long userId2, @Param("userId") Long userId, @Param("oneOnOneChat") ChatType oneOnOneChat);
    Long getChatId(@Param("userId2") Long userId2, @Param("userId") Long userId, @Param("oneOnOneChat") ChatType oneOnOneChat);
}