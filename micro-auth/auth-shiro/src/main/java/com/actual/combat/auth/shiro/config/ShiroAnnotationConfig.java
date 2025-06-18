package com.actual.combat.auth.shiro.config;

import com.actual.combat.aop.abs.bean.AbsBean;
import com.actual.combat.auth.shiro.aop.ShiroPermissions;
import com.actual.combat.auth.shiro.aop.ShiroRoles;
import com.actual.combat.auth.shiro.aop.aspect.ShiroAopAspect;
import com.actual.combat.basic.core.constant.ConfigConstant;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.shiro.authz.annotation.Logical;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


/**
 * @Author yan
 * @Date 2024/10/21 下午6:04:44
 * @Description
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Configuration
@ConfigurationProperties(prefix = ShiroAnnotationConfig.SHIRO_ANNOTATION)
public class ShiroAnnotationConfig implements AbsBean {
    public static final String SHIRO_ANNOTATION = ConfigConstant.AUTH_SHIRO_ANNOTATION_CONFIG;
    /**
     * 是否开启注解 用于测试跳过权限
     */
    private boolean enable = true;
    /**
     * 注解逻辑 默认为 and
     * {@link ShiroRoles}
     * {@link ShiroPermissions}
     * and:注解同时满足才允许访问方法
     *
     * @see Logical
     * @see ShiroAopAspect#around(ProceedingJoinPoint)
     */
    private Logical logical = Logical.AND;

    @PostConstruct
    public void init() {
        log().debug("初始化自定义权限校验");
        log().debug("设置 {}.enable=false 可跳过权限校验 用于本地测试", SHIRO_ANNOTATION);
    }

}
