package com.actual.combat.basic.im.core.service.impl.im;

import cn.hutool.core.util.ObjectUtil;

import com.actual.combat.basic.core.constant.datasource.DataSourceName;
import com.actual.combat.basic.core.enums.im.ChatType;
import com.actual.combat.basic.core.vo.user.UserVo;
import com.actual.combat.basic.user.core.service.SysUserService;
import com.actual.combat.im.domain.chat.ChatUser;
import com.actual.combat.im.domain.chat.ChatWindow;
import com.actual.combat.im.domain.im.Apply;
import com.actual.combat.im.domain.im.Friend;
import com.actual.combat.im.mapper.im.ApplyMapper;
import com.actual.combat.user.domain.SysUser;
import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.actual.combat.basic.im.core.service.chat.ChatUserService;
import com.actual.combat.basic.im.core.service.chat.ChatWindowService;
import com.actual.combat.basic.im.core.service.im.FriendService;
import com.actual.combat.basic.im.core.service.im.ApplyService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author minimalism
 * @Date 2023/8/7 0007 10:36
 * @Description
 */
@Service @DS(DataSourceName.im)
public class ApplyServiceImpl extends ServiceImpl<ApplyMapper, Apply> implements ApplyService {
//    @Resource
//    private FriendMapper friendMapper;
    @Resource
    private FriendService friendService;
    @Resource
    private ChatWindowService chatWindowService;
    @Resource
    private ChatUserService chatUserService;
    @Resource
    private ApplyMapper dao;
    @Resource
    private SysUserService userService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public UserVo applyAgree(Apply apply) {
        Long chatId = null;
        Long id = apply.getId();
        Boolean agree = apply.getAgree();
        if (agree) {
            apply = getById(id);
            Long uid = apply.getUid();
            Long tid = apply.getTid();
            List<Friend> friends = new ArrayList<>();
            for (int i = 0; i < 2; i++) {
                Friend friend = new Friend();
                boolean bool = i == 0;
                friend.setUid(bool ? uid : tid);
                friend.setFid(bool ? tid : uid);
                friends.add(friend);
                //friendMapper.insert(friend);
            }
            friendService.saveBatch(friends);

            //不存在聊天室
            ChatType oneOnOneChat = ChatType.ONE_ON_ONE_CHAT;
            ChatWindow one = chatWindowService.getChatWindow(uid, tid, oneOnOneChat);
            chatId = one == null ? null : one.getChatId();

            if (ObjectUtil.isEmpty(chatId)) {
                //创建聊天窗口
                LocalDateTime now = LocalDateTime.now();
                ChatWindow chatWindow = new ChatWindow(null, oneOnOneChat, now, null);
                chatWindowService.save(chatWindow);

                //聊天窗口关联自己和好友
                chatId = chatWindow.getChatId();
                List<ChatUser> chatUsers = new ArrayList<>();
                ChatUser chatUser = null;
                for (int i = 0; i < 2; i++) {
                    boolean bool = i == 0;
                    chatUser = new ChatUser(null, chatId, oneOnOneChat, bool ? uid : tid, now);
                    chatUsers.add(chatUser);
//                    chatUserService.save(chatUser);
                }
                chatUserService.saveBatch(chatUsers);
            }

        }

        removeById(id);

        //获取申请人信息
        SysUser byId = userService.getById(apply.getUid());

        UserVo user = new UserVo();

        user.setChatId(chatId);
        user.setNickName(byId.getNickName());
        return user;
    }
}
