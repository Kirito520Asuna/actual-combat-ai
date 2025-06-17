package com.actual.combat.seata.aop.annotation;

import io.seata.spring.annotation.GlobalTransactional;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author yan
 * @Date 2025/4/25 23:06:26
 * @Description
 */
@ToGlobal
@GlobalTransactional(rollbackFor = {Exception.class})
@Transactional(rollbackFor = {Exception.class})
@Deprecated
public @interface GlobalCustomTransactional {
}
