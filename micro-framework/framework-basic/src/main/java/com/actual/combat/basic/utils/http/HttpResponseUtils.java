package com.actual.combat.basic.utils.http;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @Author yan
 * @Date 2025/3/23 12:12:10
 * @Description
 */
public class HttpResponseUtils {

    public static HttpServletResponse getResponse() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return attributes != null ? attributes.getResponse() : null;
    }

    public static String buildRedirectUrl(String baseUrl, String rangeHeader) {
        if (rangeHeader == null || rangeHeader.isEmpty()) {
            return baseUrl;
        }
        try {
            return baseUrl + "?Range=" + URLEncoder.encode(rangeHeader, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            return baseUrl;
        }
    }
}
