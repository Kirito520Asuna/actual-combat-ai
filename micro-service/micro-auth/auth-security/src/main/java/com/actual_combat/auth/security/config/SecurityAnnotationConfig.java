package com.actual_combat.auth.security.config;

import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


/**
 * @Author yan
 * @Date 2024/10/21 下午6:04:44
 * @Description
 */
@Data @Slf4j
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Configuration @ConditionalOnBean(SecurityConfig.class)
@ConfigurationProperties(prefix = SecurityAnnotationConfig.SECURITY_ANNOTATION)
public class SecurityAnnotationConfig {
    public static final String SECURITY_ANNOTATION = "config.auth.security.annotation";
    /**
     * 是否开启注解 用于测试跳过权限
     */
    private boolean enable = true;

    @PostConstruct
    public void init() {
        log.debug("初始化自定义权限校验");
        log.debug("设置 {}.enable=false 可跳过权限校验 用于本地测试",SECURITY_ANNOTATION);
    }
}
