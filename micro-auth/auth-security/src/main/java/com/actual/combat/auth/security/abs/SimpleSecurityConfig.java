package com.actual.combat.auth.security.abs;

import cn.hutool.extra.spring.SpringUtil;
import com.actual.combat.auth.security.auth.JwtAuthSecurityFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * @Author yan
 * @Date 2025/6/13 17:22:03
 * @Description
 */
public class SimpleSecurityConfig implements AbsSecurityConfig {
    @Override
    public void addFilterBeforeList(HttpSecurity http) {
        JwtAuthSecurityFilter authFilter = null;
        try {
            authFilter = SpringUtil.getBean(JwtAuthSecurityFilter.class);
        }catch (Exception e){
            log().error("class:{},err:{}",getAClassName(),e.getMessage());
        }
        if (authFilter == null) {
            log().warn("JwtFilter is null, please make sure it's a Spring Bean");
        }
    }
}
