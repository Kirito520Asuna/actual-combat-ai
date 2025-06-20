package com.im.mapper.im;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author yan
 * @Date 2024/1/3 0003 16:01
 * @Description
 */
public class A {
    public static void main(String[] args) {
        String s = "[org.springframework.security.web.session.DisableEncodeUrlFilter@4731c9af, org.springframework.security.web.context.request.async.WebAsyncManagerIntegrationFilter@2417413c, org.springframework.security.web.context.SecurityContextPersistenceFilter@7e9589b2, org.springframework.security.web.header.HeaderWriterFilter@46f66a66, org.springframework.security.web.authentication.logout.LogoutFilter@60fd51a1, com.parent.im.filter.ApiFilter@7137cd14, com.parent.im.filter.JwtAuthenticationTokenFilter@4f2b1a2f, org.springframework.security.web.savedrequest.RequestCacheAwareFilter@402c3dce, org.springframework.security.web.servletapi.SecurityContextHolderAwareRequestFilter@1635b03f, org.springframework.security.web.authentication.AnonymousAuthenticationFilter@407858c5, org.springframework.security.web.session.SessionManagementFilter@60af38ec, org.springframework.security.web.access.ExceptionTranslationFilter@645650b1, org.springframework.security.web.access.intercept.FilterSecurityInterceptor@4d979f76]";
        String replace = s.replace("[", "").replace("]", "");
        List<String> collect = Arrays.stream(replace.split(",")).collect(Collectors.toList());
        collect.stream().forEach(one->{
            System.err.println(one);
        });
    }
}
