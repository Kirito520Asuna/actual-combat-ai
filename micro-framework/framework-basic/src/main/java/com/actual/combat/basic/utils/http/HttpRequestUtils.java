package com.actual.combat.basic.utils.http;


import com.actual.combat.basic.utils.str.StrUtils;
import jakarta.servlet.http.HttpServletRequest;

/**
 * @Author yan
 * @Date 2025/5/6 08:50:29
 * @Description
 */
public class HttpRequestUtils {
    /**
     * 获取请求路径
     *
     * @param request
     * @return
     */
    public static String getRequestSuffix(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        if (StrUtils.isNotBlank(contextPath)) {
            String url = request.getRequestURL().toString();
            if (contextPath.endsWith("/")){
                contextPath = contextPath.substring(0, contextPath.length() - 1);
            }
            String suffix = url.substring(url.indexOf(contextPath) + contextPath.length());
            return suffix;
        } else {
            String url = request.getRequestURI();
            url = url.replace("https://", "").replace("http://", "");
            String suffix = url.substring(url.indexOf("/"));
            return suffix;
        }
    }
}
