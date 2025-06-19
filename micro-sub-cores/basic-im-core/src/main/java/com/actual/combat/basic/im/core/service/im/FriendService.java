package com.actual.combat.basic.im.core.service.im;


import com.actual.combat.basic.core.vo.user.UserVo;
import com.actual.combat.im.domain.im.Friend;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * @Author minimalism
 * @Date 2023/8/7 0007 10:36
 * @Description
 */
public interface FriendService extends IService<Friend> {


    /**
     * @param userId
     * @param keyword
     * @return
     */
        List<UserVo> getFriends(Long userId, String keyword);
    }
