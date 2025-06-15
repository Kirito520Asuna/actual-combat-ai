package com.actual_combat.basic.core.abs.auth.service;

import com.actual_combat.basic.core.abs.auth.AbsAuthFilter;
import com.actual_combat.basic.core.abs.auth.AbsAuthInterceptor;
import com.actual_combat.basic.core.filter.SimpleAuthFilter;
import com.actual_combat.basic.core.interceptor.SimpleAuthInterceptor;

/**
 * @Author yan
 * @Date 2025/6/12 17:02:02
 * @Description
 */
public interface AbsAuthService {

    /**
     *
     * @return
     */
    default AbsAuthInterceptor getAuthInterceptor() {
        return new SimpleAuthInterceptor();
    }

    /**
     *
     * @return
     */

    default AbsAuthFilter getAuthFiler() {
        return new SimpleAuthFilter();
    }
}
