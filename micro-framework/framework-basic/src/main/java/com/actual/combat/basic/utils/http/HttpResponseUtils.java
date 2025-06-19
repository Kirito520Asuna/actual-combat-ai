package com.actual.combat.basic.utils.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * @Author yan
 * @Date 2025/3/23 12:12:10
 * @Description
 */
public class HttpResponseUtils {
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
