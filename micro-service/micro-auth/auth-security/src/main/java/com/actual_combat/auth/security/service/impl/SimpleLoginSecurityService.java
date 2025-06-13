package com.actual_combat.auth.security.service.impl;

import cn.hutool.extra.spring.SpringUtil;
import com.actual_combat.auth.security.service.AbsUserDetailsService;
import com.actual_combat.auth.security.utils.AuthSecurityContextUtil;
import com.actual_combat.base.core.abs.auth.service.AbstractLoginService;
import com.actual_combat.base.core.abs.auth.service.AbstractUserService;
import com.actual_combat.base.core.pojo.auth.TokenInfo;
import com.actual_combat.base.core.pojo.auth.UserInfo;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.stream.Collectors;

/**
 * @Author yan
 * @Date 2025/6/12 23:32:47
 * @Description
 */
public class SimpleLoginSecurityService implements AbstractLoginService {
    /**
     * @param userInfo
     * @return
     */
    @Override
    public TokenInfo login(UserInfo userInfo) {
        TokenInfo tokenInfo = AbstractLoginService.super.login(userInfo);

        UserInfo oneByUserName = SpringUtil.getBean(AbstractUserService.class).getOneByUserName(userInfo.getUsername());
        AuthSecurityContextUtil.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(oneByUserName.getId(),null,
                oneByUserName.getRoles().stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList())));
        return tokenInfo;
    }

    @Override
    public String getCurrentUserId() {
        return AuthSecurityContextUtil.getUserIdNoThrow();
    }

    /**
     * @param id
     */
    @Override
    public void logout(String id) {
        SpringUtil.getBean(AbsUserDetailsService.class)
                .logout(id);
    }
}
