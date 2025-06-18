package com.actual.combat.auth.shiro.abs;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.actual.combat.aop.abs.bean.AbsBean;
import com.actual.combat.auth.shiro.utils.AuthShiroContextUtil;
import com.actual.combat.basic.core.abs.auth.service.AbstractUserService;
import com.actual.combat.basic.core.constant.Roles;
import com.actual.combat.basic.core.pojo.auth.UserInfo;
import com.actual.combat.basic.exceptions.GlobalCustomException;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.List;

/**
 * @Author yan
 * @Date 2024/10/11 下午10:44:35
 * @Description
 */
public interface AbsAuthShiro extends AbsBean {
    /**
     * 授权
     */
    default AuthorizationInfo abstractDoGetAuthorizationInfo(PrincipalCollection principalCollection) {
        log().debug("开始授权");
        String username = AuthShiroContextUtil.getUsername();
        //获取UserService对象
        AbstractUserService userService = SpringUtil.getBean(AbstractUserService.class);
        //基于角色授权
        UserInfo userInfo = userService.getOneByUserName(username);
        List<String> roles = userService.getRolesById(Long.parseLong(userInfo.getId()));
        userInfo.setRoles(roles);
        SimpleAuthorizationInfo authorizationInfo = null;
        if (CollUtil.isNotEmpty(userInfo.getRoles())) {
            SimpleAuthorizationInfo info = new SimpleAuthorizationInfo();
            userInfo.getRoles().forEach(role -> {
                if (role.startsWith(Roles.roles)) {
                    info.addRole(role);
                } else if (role.startsWith(Roles.perms)) {
                    info.addStringPermission(role);
                }
            });
            authorizationInfo = info;
        }
        return authorizationInfo;
    }

    /**
     * 认证
     */

    default AuthenticationInfo abstractDoGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        // 获取用户输入的用户名和密码
        UsernamePasswordToken userToken = (UsernamePasswordToken) authenticationToken;
        String username = userToken.getUsername();
        String password = new String(userToken.getPassword());
        AbstractUserService userService = SpringUtil.getBean(AbstractUserService.class);
        UserInfo userInfo = userService.getOneByUserName(username);
        if (ObjectUtil.isEmpty(userInfo)) {
            throw new GlobalCustomException("用户不存在");
        }
        // 用户数据
        String correctPassword = userInfo.getPassword();

        boolean matchPassword = ObjectUtil.equal(password, correctPassword);
        //boolean enableEncodePassword = ShiroConfig.getEnableEncodePassword();
        //if (enableEncodePassword){
        //    matchPassword = EncodePasswordUtils.matchPassword(password, correctPassword);
        //}
        // 判断密码是否正确
        if (!matchPassword) {
            throw new GlobalCustomException("密码错误");
        }
        // 认证成功，返回身份信息
        return new SimpleAuthenticationInfo(username, correctPassword, getRealmName());
    }

    default void login(String username, String password) throws Exception {
        // shiro登录认证
        UsernamePasswordToken token = new UsernamePasswordToken(username, password);
        AuthShiroContextUtil.login(token);
    }

    /**
     * @return
     */
    String getRealmName();
}
