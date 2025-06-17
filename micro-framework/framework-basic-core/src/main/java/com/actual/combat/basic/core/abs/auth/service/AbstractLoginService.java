package com.actual.combat.basic.core.abs.auth.service;

import cn.hutool.extra.spring.SpringUtil;
import com.actual.combat.basic.core.pojo.auth.TokenInfo;
import com.actual.combat.basic.core.pojo.auth.User;
import com.actual.combat.basic.core.pojo.auth.UserInfo;

/**
 * @Author yan
 * @Date 2024/11/5 上午12:48:32
 * @Description
 */
public interface AbstractLoginService {
    /**
     * 注册账号
     *
     * @return
     */
    default User register(String nickname, String password, String password2) {
        return SpringUtil.getBean(AbstractUserService.class).register(nickname, password, password2);
    }

    /**
     * 登录
     *
     * @param user
     * @return
     */
    default TokenInfo login(UserInfo user) {
        return SpringUtil.getBean(AbstractUserService.class).login(user);
    }

    /**
     * 从redis中获取用户信息(redis中存在数据表示在线状态)
     *
     * @param id
     * @return
     */
    default User getOneRedis(String id) {
        return SpringUtil.getBean(AbstractUserService.class).getOneRedis(id);
    }

    String getCurrentUserId();

    /**
     * 账号退出(移除redis数据)
     *
     * @return
     */
    default void logout() {
        SpringUtil.getBean(getClass()).logout(getCurrentUserId());
    }

    /**
     * 账号退出(移除redis数据)
     *
     * @param id
     * @return
     */
    void logout(String id);

}
