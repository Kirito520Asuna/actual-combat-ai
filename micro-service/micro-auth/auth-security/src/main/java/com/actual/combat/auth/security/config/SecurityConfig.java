package com.actual.combat.auth.security.config;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.actual.combat.auth.security.abs.AbsSecurityConfig;
import com.actual.combat.auth.security.service.AbsUserDetailsService;
import com.actual.combat.basic.core.abs.auth.service.SimpleUserDetailsService;
import com.actual.combat.basic.core.abs.auth.config.AbsAuthSecurityConfig;
import com.actual.combat.basic.core.abs.auth.service.AbstractUserDetailsService;
import com.actual.combat.basic.core.config.jwt.JwtConfig;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * @Author yan
 * @Date 2025/6/12 23:24:35
 * @Description Spring Boot 3.x 的安全配置
 */
@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class SecurityConfig implements AbsAuthSecurityConfig {

    /**
     * 密码加密器 Bean，用于加密存储密码
     *
     * @return BCryptPasswordEncoder
     */
    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 配置 HTTP 安全设置
     *
     * @param http HttpSecurity 配置对象
     * @return SecurityFilterChain
     * @throws Exception 配置失败时抛出
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        JwtConfig jwtConfig = null;
        try {
            jwtConfig = SpringUtil.getBean(JwtConfig.class);
        } catch (Exception e) {
            log().error("class:{},err:{}", getAClassName(), e.getMessage());
            log().warn("JwtConfig is Null");
        }
        Boolean jwtOpenFilter = jwtConfig == null ? null : jwtConfig.getOpenFilter();
        Boolean jwtOpenInterceptor = jwtConfig == null ? null : jwtConfig.getOpenInterceptor();
        Boolean openFilter = ObjectUtil.defaultIfNull(jwtOpenFilter, true)
                && !ObjectUtil.defaultIfNull(jwtOpenInterceptor, false);
        String jwtPathFinal = jwtConfig == null ? null : jwtConfig.getJwtPath();

        http
                .cors(cors -> cors.configure(http)) // 启用 CORS
                .csrf(csrf -> csrf.disable()) // 禁用 CSRF
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 无状态会话
                .authorizeHttpRequests(auth -> {
                    auth.requestMatchers("/login", "/logout").permitAll(); // 允许登录和登出请求
                    if (openFilter) {
                        String jwtPath = "/jwt/**";
                        jwtPath = ObjectUtil.defaultIfNull(jwtPath, jwtPathFinal);
                        String[] paths = jwtPath.split(",");
                        auth.requestMatchers(paths).authenticated(); // JWT 路径需要认证
                    }
                    auth.anyRequest().permitAll(); // 其他请求允许访问
                });

        if (openFilter) {
            try {
                AbsSecurityConfig config = SpringUtil.getBean(AbsSecurityConfig.class);
                config.addFilterBeforeList(http); // 添加自定义过滤器
            }catch (Exception e){
                log().warn("自定义过滤器 AbsSecurityConfig is Null");
                log().error("class:{},err:{}",getAClassName(),e.getMessage());
            }
        }

        return http.build();
    }

    @Bean
    @ConditionalOnMissingBean(AbstractUserDetailsService.class)
    public AbstractUserDetailsService authUserDetailsService() {
        return new SimpleUserDetailsService();
    }

    /**
     * 配置 AuthenticationProvider，使用自定义 UserDetailsService 和 PasswordEncoder
     *
     * @return AuthenticationProvider
     */
    @Bean
    @ConditionalOnBean({AbsUserDetailsService.class, PasswordEncoder.class})
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(SpringUtil.getBean(AbsUserDetailsService.class));
        authProvider.setPasswordEncoder(SpringUtil.getBean(PasswordEncoder.class));
        return authProvider;
    }

    /**
     * 显式定义 AuthenticationConfiguration Bean
     *
     * @return AuthenticationConfiguration
     */
    @Bean
    public AuthenticationConfiguration authenticationConfiguration() {
        return new AuthenticationConfiguration();
    }

    /**
     * 配置 AuthenticationManager，注入自定义 AuthenticationProvider
     *
     * @param authenticationConfiguration 认证配置
     * @return AuthenticationManager
     * @throws Exception 配置失败时抛出
     */
    @Bean
    @ConditionalOnMissingBean(AuthenticationManager.class)
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        AuthenticationManager authenticationManager = authenticationConfiguration.getAuthenticationManager();
        // 自定义认证提供者 authenticationProvider
        AuthenticationProvider authenticationProvider = null;
        try {
            authenticationProvider = SpringUtil.getBean(AuthenticationProvider.class);
        } catch (Exception e) {
            log().error("class:{},err:{}", getAClassName(), e.getMessage());
            log().warn("自定义认证提供者 authenticationProvider is Null");
        }
        if (authenticationProvider != null) {
            // 添加自定义 AuthenticationProvider
            authenticationManager = new org.springframework.security.authentication.ProviderManager(authenticationProvider);
        }
        return authenticationManager;
    }
}