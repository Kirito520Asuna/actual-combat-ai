package com.actual_combat.base.core.abs.auth.service;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.actual_combat.aop.abs.bean.AbsBean;
import com.actual_combat.base.core.config.jwt.JwtConfig;
import com.actual_combat.base.core.constant.Roles;
import com.actual_combat.base.core.pojo.auth.TokenInfo;
import com.actual_combat.base.core.pojo.auth.User;
import com.actual_combat.base.core.pojo.auth.UserInfo;
import com.actual_combat.base.utils.jwt.JwtUtils;
import com.actual_combat.base.utils.object.ObjectUtils;

import java.util.List;


/**
 * @Author yan
 * @Date 2024/5/20 0020 17:39
 * @Description
 */
public interface AbstractUserService extends AbsBean {

    default UserInfo getOneByUserName(String userName) {
        return new UserInfo()
                .setId("-1")
                .setUsername(userName)
                //原密码123456 加密形成
                .setPassword("$2a$10$fLuXlTai3PEgUNMySVRN7eR9JAZcsrQmylFT6jdoRJLbjTSj2o1FO")
                .setRoles(CollUtil.newArrayList("admin"))
                .setImage("")
                .setNickname("admin")
                .setAccountStatus(true);
    }

    /**
     * 登录
     *
     * @param user
     * @return
     */
    default TokenInfo login(UserInfo user) {
        User userRe = SpringUtil.getBean(AbstractUserService.class).login(user.getUsername(), user.getPassword());
        String id = userRe.getUser().getId();
        JwtConfig jwtConfig = SpringUtil.getBean(JwtConfig.class);
        String tokenName = jwtConfig.getTokenName();
        String refreshTokenName = jwtConfig.getRefreshTokenName();
        Boolean openTwoToken = ObjectUtils.defaultIfEmpty(jwtConfig.getEnableTwoToken(), false);
        String token = JwtUtils.createJWT(id);
        TokenInfo tokenInfo = new TokenInfo();
        tokenInfo.setTokenName(tokenName)
                .setEnableTwoToken(openTwoToken)
                .setToken(token);
        if (openTwoToken) {
            String refreshToken = JwtUtils.createJWT(id, JwtUtils.getLONG_JWT_TTL());
            tokenInfo.setRefreshTokenName(refreshTokenName)
                    .setRefreshToken(refreshToken);
        }
        return tokenInfo;
    }

    default User login(String username, String password) {
        return null;
    }

    default User register(String nickname, String password, String password2) {
        return null;
    }

    default User getOneRedis(String id) {
        return null;
    }

    default List<String> getRolesById(Long userId) {
        List<String> list = CollUtil.newArrayList();
        List<String> roles = getRoles(userId);
        roles.stream().forEach(role -> {
            if ((!role.startsWith(Roles.roles))) {
                role = new StringBuffer(Roles.roles).append(role).toString();
            }
            list.add(role);
        });
        return list;
    }

    /**
     * 获取角色
     *
     * @param userId
     * @return
     */
    default List<String> getRoles(Long userId) {
        log().info("getRoles:{}" ,userId);
        return CollUtil.newArrayList();
    }

    default List<String> getPermsById(Long userId) {
        List<String> list = getPerms(userId);
        list.stream().forEach(perm -> {
            if (!perm.startsWith(Roles.perms)) {
                perm = new StringBuffer(Roles.perms).append(perm).toString();
            }
            list.add(perm);
        });
        return list;
    }

    /**
     * 获取菜单权限
     *
     * @param userId
     * @return
     */
    default List<String> getPerms(Long userId) {
        log().info("getPerms:{}" , userId);
        return CollUtil.newArrayList();
    }

    default List<String> getAnyRolesById(Long userId) {
        List<String> arrayList = CollUtil.newArrayList();
        arrayList.addAll(getRolesById(userId));
        arrayList.addAll(getPermsById(userId));
        return arrayList;
    }

}
