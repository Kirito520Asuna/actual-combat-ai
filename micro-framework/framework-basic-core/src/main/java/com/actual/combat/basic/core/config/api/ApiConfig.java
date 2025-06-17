package com.actual.combat.basic.core.config.api;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONConfig;
import com.actual_combat.aop.abs.bean.AbsBean;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Resource;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.ObjectUtils;;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author yan
 * @Date 2024/5/15 0015 17:13
 * @Description
 */
@Slf4j
@Data @NoArgsConstructor
@AllArgsConstructor
public class ApiConfig implements AbsBean {
    public static JSONConfig JSON_CONFIG = JSONConfig.create().setIgnoreNullValue(false);
    @Resource
    private ObjectMapper objectMapper;
    @Value("${spring.application.name: }")
    public String springApplicationName;
    @Value("${salt.api:API_SALT}")
    public String apiSalt;
    @Value("${asName.sign:sign}")
    public String signAsName;
    @Value("${asName.timestamp:timestamp}")
    public String timestampAsName;
    @Value("${api.path:/api/}")
    private String apiPath;
    @Value("${ip.whitelist:127.0.0.1}")
    private String ipWhitelist;
    @Value("${ip.blacklist: }")
    private String ipBlackList;
    @Value("${server.servlet.context-path: }")
    private String contextPath;
    @Value("${sign.enable:true}")
    private Boolean signEnable;
    @Value("${sign.multiple.enable:false}")
    private Boolean signMultipleEnable;
    @Value("${sign.timeOut:10}")
    private Long signTimeOut;
    @Value("${server.port:8080}")
    private String serverPort;

    public Boolean getSignMultipleEnable() {
        if (ObjectUtil.isEmpty(signMultipleEnable)){
            signMultipleEnable = false;
        }
        return signMultipleEnable;
    }

    public String getApiPath() {
        String apiPath = this.apiPath;
        if (StrUtil.isBlank(apiPath)) {
            apiPath = "/api/";
        }
        if (!apiPath.startsWith("/")) {
            apiPath = new StringBuffer("/").append(apiPath).toString();
        }
        if (!apiPath.endsWith("/")) {
            apiPath = new StringBuffer(apiPath).append("/").toString();
        }
        return apiPath;
    }

    /**
     * 验证ip是否在白名单中
     *
     * @param ip
     * @return
     */
    public boolean verifyIpWhiteList(String ip) {
        String temp = ipWhitelist;
        List<String> list = CollUtil.newArrayList();
        if (ObjectUtil.isNotEmpty(temp)) {
            list.addAll(Arrays.stream(temp.replace(" ", "").split(",")).collect(Collectors.toList()));
        }
        return list.contains(ip);
    }

    public String getPath() {
        String path = contextPath;
        path = ObjectUtils.isEmpty(path) ? "" : path;
        return path;
    }

    /**
     * 获取url中的路径
     *
     * @param url
     * @return
     */
    public String getUrl(String url) {
        String path = getPath();
        if (ObjectUtils.isEmpty(path)) {
            path = getServerPort();
        } else {
            path = path.endsWith("/") ? path : path + "/";
        }
        int startIndex = url.indexOf(path);
        startIndex = startIndex == -1 ? 0 : startIndex;
        String substring = url.substring(startIndex, url.length());
        String s = substring;
        if (!s.startsWith("/")) {
            s = new StringBuffer("/").append(s).toString();
        }
        return s;
    }
}
