package com.actual_combat.auth.security.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.actual_combat.basic.enums.ApiCode;
import com.actual_combat.basic.exceptions.GlobalCustomException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author yan
 * @Date 2024/7/25 0025 10:45:09
 * @Description
 */
@Slf4j
public class AuthSecurityContextUtil {
    // 获取SecurityContext
    public static SecurityContext getContext() {
        return SecurityContextHolder.getContext();
    }
    // 获取当前登录用户的Authentication对象
    public static Authentication getAuthentication() {
        return getContext().getAuthentication();
    }
    // 获取用户Id
    public static String getUserId() {
        String userId = getUserIdNoThrow();
        log.info("userId==>: {}", userId);
        if (StrUtil.isBlank(userId)){
            throw new GlobalCustomException(ApiCode.UNAUTHORIZED);
        }
        return userId;
    }
    public static String getUserIdNoThrow() {
        String userId = getAuthentication().getName();
        if (ObjectUtil.equal("anonymousUser", userId)){
            userId = null;
        }
        return userId;
    }
    // 获取权限信息
    public static Object getPrincipal() {
        return getAuthentication().getPrincipal();
    }
    //获取权限key
    public static Collection<? extends GrantedAuthority> getAuthorities(){
        Collection<? extends GrantedAuthority> authorities = CollUtil.newArrayList();
        try {
            authorities = getAuthentication().getAuthorities();
        }catch (Exception e){
            log.error("err : {}",e.getMessage());
        }
        return authorities;
    }
    public static List<String> getAnyRoles(){
        List<String> roles = CollUtil.newArrayList();
        try {
            roles.addAll(getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()));
        }catch (Exception e){
            log.error("err : {}",e.getMessage());
        }
        return roles;
    }

}
