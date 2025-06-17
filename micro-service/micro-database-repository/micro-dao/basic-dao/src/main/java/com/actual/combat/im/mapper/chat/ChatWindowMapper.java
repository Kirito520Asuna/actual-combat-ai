package com.actual.combat.im.mapper.chat;


import com.actual.combat.basic.core.enums.im.ChatType;
import com.actual.combat.im.domain.chat.ChatWindow;
import com.actual.combat.mp.abs.mapper.MpMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author yan
 * @Date 2023/8/10 0010 14:22
 * @Description
 */
@Mapper
public interface ChatWindowMapper extends MpMapper<ChatWindow> {
    ChatWindow getChatWindow(@Param("uid") Long uid, @Param("tid") Long tid, @Param("chatType") ChatType chatType);

}