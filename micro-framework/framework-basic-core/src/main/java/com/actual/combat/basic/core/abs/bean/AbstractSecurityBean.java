package com.actual.combat.basic.core.abs.bean;

import com.actual.combat.aop.abs.bean.AbsBean;

/**
 * @Author yan
 * @Date 2025/6/14 01:55:56
 * @Description
 */
public interface AbstractSecurityBean extends AbsBean {
    @Override
    default void init() {
        AbsBean.super.init();
    }
}
