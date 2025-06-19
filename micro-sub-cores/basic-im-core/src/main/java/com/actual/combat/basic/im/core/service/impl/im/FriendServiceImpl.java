package com.actual.combat.basic.im.core.service.impl.im;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.actual.combat.basic.core.constant.datasource.DataSourceName;
import com.actual.combat.basic.core.enums.im.ChatType;
import com.actual.combat.basic.core.vo.user.UserVo;
import com.actual.combat.basic.user.core.service.SysUserService;
import com.actual.combat.basic.utils.bean.CustomBeanUtils;
import com.actual.combat.basic.utils.object.ObjectUtils;
import com.actual.combat.im.domain.chat.ChatUser;
import com.actual.combat.im.domain.chat.ChatWindow;
import com.actual.combat.im.domain.im.Friend;
import com.actual.combat.im.mapper.chat.ChatUserMapper;
import com.actual.combat.im.mapper.chat.ChatWindowMapper;
import com.actual.combat.im.mapper.im.FriendMapper;
import com.actual.combat.user.domain.SysUser;
import com.actual.combat.user.mapper.SysUserDao;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.dynamic.datasource.annotation.DSTransactional;
import com.baomidou.dynamic.datasource.toolkit.DynamicDataSourceContextHolder;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.actual.combat.basic.im.core.service.im.FriendService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author minimalism
 * @Date 2023/8/7 0007 10:36
 * @Description
 */
@Service
@DS(DataSourceName.im)
@DSTransactional
public class FriendServiceImpl extends ServiceImpl<FriendMapper, Friend> implements FriendService {

    @Resource
    private SysUserDao userMapper;
    @Resource
    private SysUserService userService;
    @Resource
    private ChatWindowMapper chatWindowMapper;
    @Resource
    private ChatUserMapper chatUserMapper;

    @Override
    public List<UserVo> getFriends(Long userId, String keyword) {
        SysUser user1 = new SysUser();
        user1.setUserId(userId);
        user1.setUserName(keyword);
        user1.setNickName(keyword);
        List<UserVo> list = CollUtil.newArrayList();

        List<Long> friendIds = list(Wrappers.lambdaQuery(Friend.class).select(Friend::getFid).eq(Friend::getUid, userId))
                .stream().map(Friend::getFid).collect(Collectors.toList());

        DynamicDataSourceContextHolder.push(DataSourceName.user);
        try {
            //因为测试时使用了不同数据源 sys_user 和 friend 在不同数据源 因此设置 commonDatasource = false
            boolean commonDatasource = false;
            list.addAll(
                    userService.getFriends(user1,friendIds,commonDatasource)
                            .stream().filter(ObjectUtils::isNotEmpty)
                            .map(o -> {
                                UserVo vo = new UserVo();
                                CustomBeanUtils.copyPropertiesIgnoreNull(o, vo);
                                return vo;
                            }).collect(Collectors.toList())
            );
        } finally {
            DynamicDataSourceContextHolder.clear();
        }

        LambdaQueryWrapper<ChatUser> chatUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ChatType oneOnOneChat = ChatType.ONE_ON_ONE_CHAT;
        chatUserLambdaQueryWrapper.select(ChatUser::getChatId)
                .eq(ChatUser::getChatType, oneOnOneChat)
                .eq(ChatUser::getUserId, userId);
        List<ChatUser> chatUsers = chatUserMapper.selectList(chatUserLambdaQueryWrapper);

        List<Long> chatIds = chatUsers.stream().map(ChatUser::getChatId).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(chatIds)) {
            chatUserLambdaQueryWrapper = new LambdaQueryWrapper<>();
            chatUserLambdaQueryWrapper
                    .ne(ChatUser::getUserId, userId)
                    .in(ChatUser::getId, chatIds);
            chatUsers = chatUserMapper.selectList(chatUserLambdaQueryWrapper);

            chatIds = chatUsers.stream().map(ChatUser::getChatId).collect(Collectors.toList());
        }

        if (CollectionUtil.isNotEmpty(list) && CollectionUtils.isEmpty(chatIds)) {
            //有好友没有创建聊天室

            list.stream().forEach(user -> {
                //查询俩者私聊共同chatId并返回
                Long chatId = chatUserMapper.getChatId(user.getUserId(), userId, oneOnOneChat);

                if (ObjectUtil.isEmpty(chatId)) {
                    //创建聊天窗口
                    LocalDateTime now = LocalDateTime.now();
                    ChatWindow chatWindow = new ChatWindow(null, oneOnOneChat, now, null);
                    chatWindowMapper.insert(chatWindow);

                    //聊天窗口关联自己和好友
                    chatId = chatWindow.getChatId();
                    ChatUser chatUser = new ChatUser(null, chatId, oneOnOneChat, userId, now);
                    chatUserMapper.insert(chatUser);

                    chatUser.setId(null);
                    chatUser.setUserId(user.getUserId());
                    chatUserMapper.insert(chatUser);
                }


                user.setChatId(chatId);
            });

        } else {
            if ((CollectionUtil.isNotEmpty(chatUsers)) && (CollectionUtil.isNotEmpty(list))) {
                chatUsers.stream().forEach(chatUser -> {
                    for (UserVo user : list) {
                        if (user.getUserId().equals(chatUser.getUserId())) {
                            user.setChatId(chatUser.getChatId());
                            break;
                        }
                    }
                });
            }
        }

        return list;
    }
}
