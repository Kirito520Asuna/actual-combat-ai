package com.actual.combat.basic.im.core.service.impl.im;

import com.actual.combat.im.domain.im.Message;
import com.actual.combat.im.mapper.im.MessageMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.actual.combat.basic.im.core.service.im.MessageService;
import org.springframework.stereotype.Service;


/**
 * @Author minimalism
 * @Date 2023/8/7 0007 10:36
 * @Description
 */
@Service
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

}
