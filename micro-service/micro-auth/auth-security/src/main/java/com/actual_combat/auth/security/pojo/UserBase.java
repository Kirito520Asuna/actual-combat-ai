package com.actual_combat.auth.security.pojo;

import cn.hutool.core.collection.CollUtil;
import com.actual_combat.basic.core.pojo.auth.User;
import com.actual_combat.basic.core.pojo.auth.UserInfo;
import com.alibaba.fastjson.annotation.JSONField;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author yan
 * @Date 2023/5/12 0012 16:18
 * @Description
 */
@Data
@NoArgsConstructor
@AllArgsConstructor @Accessors(chain = true)
public class UserBase extends User implements UserDetails {

    /**
     * 权限信息对象
     */
    @JsonIgnore//排除展示
    @JSONField(serialize = false)//serialize = false不会序列化防止设置到redis报错
    private List<GrantedAuthority> authorities;

    public UserBase(UserInfo userInfo, List<String> roles) {
        super(userInfo, roles);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<String> roles = getRoles();
        if (authorities == null && CollUtil.isNotEmpty(roles)) {
            //把roles中的String类型的权限信息封装成SimpleGrantedAuthority对象
            authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .distinct().collect(Collectors.toList());
        }
        return authorities;
    }

    /*判断帐号是否已经过期*/
    @Override
    public boolean isAccountNonExpired() {
        return getAccountNonExpired();
    }

    /*判断帐号是否已被锁定*/
    @Override
    public boolean isAccountNonLocked() {
        return getAccountNonLocked();
    }

    /*判断用户凭证是否已经过期*/
    @Override
    public boolean isCredentialsNonExpired() {
        return getCredentialsNonExpired();
    }

    /*用户是否可用*/
    @Override
    public boolean isEnabled() {
        return getEnabled();
    }


}
