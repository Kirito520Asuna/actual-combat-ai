package com.actual.combat.basic.openfeign.config.seata;

import com.actual_combat.aop.abs.bean.AbsBean;
import io.seata.config.springcloud.EnableSeataSpringConfig;
import org.springframework.context.annotation.Configuration;

/**
 * @Author yan
 * @Date 2024/5/15 0015 15:01
 * @Description
 */
@Configuration
@EnableSeataSpringConfig
public class SeataConfig implements AbsBean {
}
