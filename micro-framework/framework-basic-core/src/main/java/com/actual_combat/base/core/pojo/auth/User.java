package com.actual_combat.base.core.pojo.auth;

import com.actual_combat.base.utils.object.ObjectUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * @Author yan
 * @Date 2024/11/5 上午12:36:58
 * @Description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class User {
    //判断帐号是否已经过期
    private Boolean accountNonExpired = true;
    //判断帐号是否已被锁定
    private Boolean accountNonLocked = true;
    //判断用户凭证是否已经过期
    private Boolean credentialsNonExpired = true;
    //用户是否可用
    private Boolean enabled;
    private String username;
    private String password;
    /**
     * 用户
     */
//    @JsonIgnore//排除展示
//    @JSONField(serialize = false)
    private UserInfo user;
    /**
     * 权限信息字符串
     */
    private List<String> roles;
    public User(UserInfo userInfo, List<String> roles){
        this.user = userInfo;
        this.roles = roles;
        if (ObjectUtils.isNotEmpty(userInfo)){
            this.username = userInfo.getUsername();
            this.password = userInfo.getPassword();
            this.enabled = userInfo.getAccountStatus();
        }
    }
}
