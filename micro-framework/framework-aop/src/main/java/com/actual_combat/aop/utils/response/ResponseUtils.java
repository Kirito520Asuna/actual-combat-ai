package com.actual_combat.aop.utils.response;

import cn.hutool.core.collection.CollUtil;
import com.google.common.collect.Maps;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

/**
 * @Author yan
 * @Date 2024/10/20 上午5:43:58
 * @Description
 */
public class ResponseUtils {
    public static void responsePush(HttpServletResponse response, String contentType, String json) throws IOException {
        // 设置返回的错误状态码
        response.setStatus(HttpServletResponse.SC_OK);

        // 返回 JSON 格式的错误消息
        response.setContentType(contentType);
        PrintWriter out = response.getWriter();

        out.write(json);
        out.flush();
        out.close();
    }

    public static void responseSetHeader(HttpServletResponse response, Map<String,String> map) {
        if (CollUtil.isEmpty(map)) {
            map = Maps.newLinkedHashMap();
        }
        for (String key : map.keySet()) {
            response.setHeader(key, map.get(key));
        }
    }
}
