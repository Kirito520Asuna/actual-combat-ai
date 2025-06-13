package com.actual_combat.auth.shiro.pojo;

import com.actual_combat.auth.shiro.abs.AbsAuthShiro;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;


/**
 * @Author yan
 * @Date 2024/10/11 下午9:55:10
 * @Description
 */
public class AuthRealm extends AuthorizingRealm implements AbsAuthShiro {
    @Override
    public AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        return AbsAuthShiro.super.abstractDoGetAuthorizationInfo(principalCollection);
    }

    @Override
    public AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        return AbsAuthShiro.super.abstractDoGetAuthenticationInfo(authenticationToken);
    }

    @Override
    public String getRealmName() {
        return getName();
    }
}
