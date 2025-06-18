package com.actual.combat.auth.shiro.config;

import cn.hutool.extra.spring.SpringUtil;
import com.actual.combat.auth.shiro.abs.AuthShiroConfig;
import jakarta.servlet.Filter;
import org.apache.shiro.authc.Authenticator;
import org.apache.shiro.authz.Authorizer;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.security.interceptor.AuthorizationAttributeSourceAdvisor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * @Author yan
 * @Date 2024/10/11 下午9:52:43
 * @Description
 */
@Configuration
public class ShiroConfig implements AuthShiroConfig {


    @Value("${shiro.encodePassword:false}")
    private Boolean enableEncodePassword;

    public static boolean getEnableEncodePassword() {
        Boolean encodePassword = SpringUtil.getBean(ShiroConfig.class).enableEncodePassword;
        encodePassword = encodePassword == null ? false : encodePassword;
        return encodePassword;
    }


    @Bean
    @Override
    @ConditionalOnMissingBean(SessionManager.class)
    public SessionManager sessionManager() {
        return AuthShiroConfig.super.sessionManager();
    }

    @Bean
    @Override
    @ConditionalOnMissingBean(Realm.class)
    public Realm authRealm() {
        return AuthShiroConfig.super.authRealm();
    }

    @Bean
    @ConditionalOnBean(Realm.class)
    public Authorizer authorizer() {
        Realm realm = SpringUtil.getBean(Realm.class);
        return AuthShiroConfig.super.authorizer(realm);
    }

    //开启shiro权限注解
    @Bean
    @Override
    public AuthorizationAttributeSourceAdvisor authorizationAttributeSourceAdvisor(WebSecurityManager webSecurityManager) {
        return AuthShiroConfig.super.authorizationAttributeSourceAdvisor(webSecurityManager);
    }

    @Bean
    @Override
    public Authenticator authenticator() {
        return AuthShiroConfig.super.authenticator();
    }

    //配置securityManager的实现类，变向的配置了securityManager
    @Bean
    public WebSecurityManager webSecurityManager() {
        Realm realm = SpringUtil.getBean(Realm.class);
        return AuthShiroConfig.super.securityManager(realm);
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilterFactoryBean(WebSecurityManager webSecurityManager) {
        return AuthShiroConfig.super.shiroFilterFactoryBean(webSecurityManager);
    }

    @Override
    public Map<String, Filter> getFilters() {
        return AuthShiroConfig.super.getFilters();
    }

    @Override
    public Map<String, String> getFilterChainDefinitionMap() {
        return AuthShiroConfig.super.getFilterChainDefinitionMap();
    }
}
