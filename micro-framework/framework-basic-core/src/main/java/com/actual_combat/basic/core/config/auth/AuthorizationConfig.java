package com.actual_combat.basic.core.config.auth;

import com.actual_combat.aop.abs.bean.AbsBean;
import com.actual_combat.basic.core.constant.Roles;
import com.actual_combat.basic.utils.object.ObjectUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @Author yan
 * @Date 2024/11/5 上午1:25:41
 * @Description
 */
@Configuration
@Data
@Accessors(chain = true)
@NoArgsConstructor
@AllArgsConstructor
public class AuthorizationConfig implements AbsBean {

    @Value("${config.auth.shiro.enable:false}")
    private boolean enableShiro = false;
    @Value("${config.auth.security.enable:true}")
    private boolean enableSecurity = true;
    @Value("${config.auth.admin.key:admin}")
    private String adminKey = "admin" ;

    public String getAdminKey() {
        String admin = adminKey.startsWith(Roles.roles) ? adminKey : new StringBuffer(Roles.roles).append(adminKey).toString();
        return admin;
    }

    public boolean isAdmin(List<String> roles) {
        boolean isAdmin = false;
        for (String role : roles) {
            isAdmin = isAdmin(role);
            if (isAdmin) {
                break;
            }
        }
        return isAdmin;
    }

    public boolean isAdmin(String key) {
        String property = getAdminKey();
        String admin = ObjectUtils.defaultIfEmpty(property, "admin");
        key = ObjectUtils.defaultIfEmpty(key, "");

        if (!key.startsWith(Roles.roles)) {
            key = new StringBuffer(Roles.roles).append(key).toString();
        }

        if (!admin.startsWith(Roles.roles)) {
            admin = new StringBuffer(Roles.roles).append(admin).toString();
        }

        boolean isAdmin = ObjectUtils.equals(admin, key);
        if (isAdmin) {
            log().debug("当前用户是管理员");
        }
        return isAdmin;
    }
}
