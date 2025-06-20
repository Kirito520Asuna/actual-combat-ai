package com.actual.combat.basic.im.core.service.impl.datasource;

import com.actual.combat.aop.abs.bean.AbsBean;
import com.actual.combat.dynamic_datasource.abs.AbsDynamicDataSource;

/**
 * @Author minimalism
 * @Date 2024/10/25 上午9:06:30
 * @Description
 */
public class SimpleDynamicDataSource implements AbsDynamicDataSource, AbsBean {
    @Override
    public String getDataSourceName() {
        return null;
    }
}
