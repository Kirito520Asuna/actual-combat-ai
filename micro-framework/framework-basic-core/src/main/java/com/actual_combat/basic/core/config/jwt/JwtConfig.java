package com.actual_combat.basic.core.config.jwt;

import cn.hutool.extra.spring.SpringUtil;
import com.actual_combat.aop.abs.bean.AbsBean;
import com.actual_combat.basic.core.constant.ExpressionConstants;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;


/**
 * @Author yan
 * @Date 2024/10/14 上午12:40:00
 * @Description
 */
@Slf4j
@Data
@NoArgsConstructor
@AllArgsConstructor
public class JwtConfig implements AbsBean {
    /**
     * 默认token名称
     */
    @Value("${common.jwt.tokenName:token}")
    //@JsonProperty("tokenName")
    private String tokenName = "token";
    /**
     * 默认刷新Token名称
     */
    @Value("${common.jwt.refreshTokenName:refreshToken}")
    //@JsonProperty("refreshTokenName")
    private String refreshTokenName = "refreshToken";
    /**
     * 是否开启双token
     */
    @Value("${common.jwt.enableTwoToken:false}")
    private Boolean enableTwoToken = false;
    /**
     * 单token通过后 再次验证过期时间时长 以便于刷新token(分钟为单位)
     */
    @Value("${common.jwt.refreshOneToken:5}")
    private Long refreshOneTokenLong = 5l;
    @Value("${common.jwt.path:/jwt/**}")
    private String jwtPath = "/jwt/**";

    @Value(ExpressionConstants.filterExpression)
    private Boolean openFilter = true;
    private Boolean openInterceptor = !openFilter;

    @Override
    @PostConstruct
    public void init() {
        log().debug("[init]-[Config]-[Jwt]::[{}]: ", getAClassName());
        Environment env = SpringUtil.getBean(Environment.class);
        Boolean openFilter = env.getProperty("common.openFilter", Boolean.class, true);
        this.openInterceptor = !openFilter;
    }


}
