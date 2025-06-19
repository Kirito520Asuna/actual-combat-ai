package com.actual.combat.basic.im.core.service.impl.chat;

import com.actual.combat.basic.core.constant.datasource.DataSourceName;
import com.actual.combat.im.domain.chat.ChatUser;
import com.actual.combat.im.mapper.chat.ChatUserMapper;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.actual.combat.basic.im.core.service.chat.ChatUserService;
import org.springframework.stereotype.Service;

/**
 * @Author minimalism
 * @Date 2023/8/10 0010 14:22
 * @Description
 */
@Service @DS(DataSourceName.im)
public class ChatUserServiceImpl extends ServiceImpl<ChatUserMapper, ChatUser> implements ChatUserService {

}
