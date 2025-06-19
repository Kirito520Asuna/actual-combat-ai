package com.actual.combat.basic.core.abs.auth.service;

import com.actual.combat.basic.core.abs.auth.AbsAuthFilter;
import com.actual.combat.basic.core.abs.auth.AbsAuthInterceptor;
import com.actual.combat.basic.core.filter.SimpleAuthFilter;
import com.actual.combat.basic.core.interceptor.SimpleAuthInterceptor;
import com.actual.combat.basic.exceptions.GlobalConfigException;
import com.actual.combat.basic.exceptions.GlobalCustomException;

/**
 * @Author yan
 * @Date 2025/6/12 17:02:02
 * @Description
 */
public interface AbsAuthService {

    /**
     * @return
     */
    default AbsAuthInterceptor getAuthInterceptor() {
        return new SimpleAuthInterceptor();
    }

    /**
     * @return
     */

    default AbsAuthFilter getAuthFiler() {
        return new SimpleAuthFilter();
    }


    default boolean matchPassword(String plainPassword, String encodedPassword) {
        throw new GlobalConfigException("not implement");
    }

    default String encodePassword(String plainPassword) {
        throw new GlobalConfigException("not implement");
    }

    default String encodePassword(String password, String password1) throws Exception {
        if (password.equals(password1)) {
            //密码加密
            String encode = encodePassword(password);
            return encode;
        } else {
            throw new GlobalCustomException("密码不一致");
        }
    }
}
