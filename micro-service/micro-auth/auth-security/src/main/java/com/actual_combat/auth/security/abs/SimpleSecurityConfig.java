package com.actual_combat.auth.security.abs;

import cn.hutool.extra.spring.SpringUtil;
import com.actual_combat.auth.security.auth.JwtFilter;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * @Author yan
 * @Date 2025/6/13 17:22:03
 * @Description
 */
public class SimpleSecurityConfig implements AbsSecurityConfig {
    @Override
    public void addFilterBeforeList(HttpSecurity http) {
        JwtFilter jwtFilter = null;
        try {
            jwtFilter = SpringUtil.getBean(JwtFilter.class);
        }catch (Exception e){
            log().error("class:{},err:{}",getAClassName(),e.getMessage());
        }
        if (jwtFilter == null) {
            log().warn("JwtFilter is null, please make sure it's a Spring Bean");
        }
    }
}
