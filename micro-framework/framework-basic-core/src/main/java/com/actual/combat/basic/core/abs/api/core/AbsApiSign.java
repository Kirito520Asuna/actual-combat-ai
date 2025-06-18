package com.actual.combat.basic.core.abs.api.core;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.servlet.JakartaServletUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.actual.combat.aop.abs.bean.AbsBean;
import com.actual.combat.aop.utils.gateway.GatewayUtils;
import com.actual.combat.basic.core.abs.api.service.AbstractApiSaltService;
import com.actual.combat.basic.core.config.api.ApiConfig;
import com.actual.combat.basic.core.pojo.api.SaltInfo;
import com.actual.combat.basic.core.pojo.http.CachedBodyHttpServletRequest;
import com.actual.combat.basic.enums.ApiCode;
import com.actual.combat.basic.enums.Openfeign;
import com.actual.combat.basic.utils.api.ApiUtil;
import com.actual.combat.basic.exceptions.GlobalCustomException;
import com.actual.combat.basic.utils.api.SingleSignature;
import com.actual.combat.basic.utils.date.DateUtils;
import com.alibaba.fastjson2.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import lombok.SneakyThrows;
import org.springframework.util.ObjectUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author yan
 * @Date 2024/5/18 0018 2:36
 * @Description
 */
public interface AbsApiSign extends AbsBean {
    ObjectMapper objectMapper = ApiUtil.getObjectMapper();

    default ObjectMapper getObjectMapper() {
        return objectMapper;
    }

    /**
     * 1. 获取 ContentCachingRequestWrapper
     *
     * @param request
     * @return
     */
    default ContentCachingRequestWrapper getContentCachingRequestWrapper(HttpServletRequest request) {
        return new ContentCachingRequestWrapper(request);
    }

    /**
     * 检查验签
     */
    default void checkApi(HttpServletRequest request, CachedBodyHttpServletRequest cachedBodyHttpServletRequest) {

        ApiConfig apiConfig = null;
        try {
            apiConfig = SpringUtil.getBean(ApiConfig.class);
        } catch (Exception e) {
            log().warn("ApiConfig未配置==> err:{}", e.getMessage());
        }
        if (apiConfig == null) {
            return;
        }
        String springApplicationName = apiConfig.getSpringApplicationName();
        String apiSalt = apiConfig.getApiSalt();
        String signAsName = apiConfig.getSignAsName();
        String timestampAsName = apiConfig.getTimestampAsName();
        String apiPath = apiConfig.getApiPath();
        Boolean signEnable = apiConfig.getSignEnable();
        Boolean signMultipleEnable = apiConfig.getSignMultipleEnable();
        Long signTimeOut = apiConfig.getSignTimeOut();

        //时间戳验证
        String url = "";
        StringBuffer requestURL = request.getRequestURL();
        String remoteAddr = request.getRemoteAddr();
        String clientIP = JakartaServletUtil.getClientIP(request);
        getLogger().info("请求ip:{},clientIP:{}", remoteAddr, clientIP);
        if (!ObjectUtils.isEmpty(requestURL)) {
            url = requestURL.toString();
        }

        String s = apiConfig.getUrl(url);
        boolean apiPathBoolean = s.contains(apiPath);
        if (verifyIpBlackList(remoteAddr, apiConfig.getIpBlackList())) {
            log().error("ip={}黑名单", remoteAddr);
            throw new GlobalCustomException("ip 非法访问");
        } else if (apiPathBoolean) {
            log().info("路径符合验签逻辑");
            if (verifyIpWhiteList(remoteAddr, apiConfig.getIpWhitelist())) {
                log().info("ip白名单");
                //白名单
            } else if (ObjectUtil.isNotEmpty(signEnable) && signEnable) {
                log().info("api验签-已开启");
                verifyTimestamp(request, timestampAsName, signTimeOut);
                //验签
                List<String> exCollection = CollUtil.newArrayList(timestampAsName, signAsName);
                List<Boolean> verifySignList = CollUtil.newArrayList();

                List<SaltInfo> apiSaltList = CollUtil.newArrayList();
                AbstractApiSaltService apiSaltService = SpringUtil.getBean(AbstractApiSaltService.class);
                Collection<SaltInfo> saltList = apiSaltService.getSaltList();
                if (CollUtil.isNotEmpty(saltList)) {
                    apiSaltList.addAll(saltList);
                }

                if ((!signMultipleEnable) && StrUtil.isNotBlank(springApplicationName)) {
                    apiSaltList = apiSaltList.stream()
                            .filter(o -> ObjectUtil.equals(o.getServiceName(), springApplicationName))
                            .collect(Collectors.toList());
                    if (CollUtil.isEmpty(apiSaltList)) {
                        SaltInfo saltInfo = new SaltInfo().setSalt(apiSalt).setServiceName("通用");
                        apiSaltList.add(saltInfo);
                    }
                }

                boolean verifySign = false;
                for (SaltInfo salt : apiSaltList) {
                    String serviceName = salt.getServiceName();
                    try {
                        verifySign = verifySign(cachedBodyHttpServletRequest, signAsName, salt.getSalt(), serviceName, null, exCollection);
                        if (verifySign) {
                            log().info("[{}]==>验签通过", serviceName);
                            break;
                        }
                    } catch (GlobalCustomException e) {
                        log().info("<==验签失败==>{}", e.getMessage());
                    } finally {
                        verifySignList.add(verifySign);
                    }
                }

                if (!verifySignList.contains(true)) {
                    log().error("<==验签失败==>");
                    throw new GlobalCustomException("签名不合法");
                } else {
                    log().info("验签通过");
                }

            }
        } else {
            log().info("非api验签");
        }
    }

    /**
     * 1. 验证请求时间戳
     *
     * @param request
     * @param timestampAsName 时间戳名称
     * @param signTimeOut     时间戳超时时间(单位: 分钟)
     * @return
     */
    default String verifyTimestamp(HttpServletRequest request, String timestampAsName, Long signTimeOut) {
        //String timestampHeader = request.getHeader(timestampAsName);
        String timestampHeader = request.getHeader(timestampAsName);
        if (StrUtil.isBlank(timestampHeader) ||
                Math.abs(Duration.between(DateUtils.longToLocalDateTime(Long.parseLong(timestampHeader)), LocalDateTime.now()).toMinutes()) >= signTimeOut) {
            if (StrUtil.isNotBlank(timestampHeader)) {
                log().error("{}=={},{},{}", timestampAsName, timestampHeader,
                        System.currentTimeMillis(),
                        Math.abs(Duration.between(DateUtils.longToLocalDateTime(Long.parseLong(timestampHeader)), LocalDateTime.now()).toMinutes()));
            } else {
                log().error("timestampAsName is null");
            }
            throw new GlobalCustomException(ApiCode.VALIDATE_FAILED, "请求时间戳不合法");
        }
        return timestampHeader;
    }

    @SneakyThrows
    default boolean verifySign(HttpServletRequest request, String signAsName, String salt, String serviceName, String url, Collection<String> exCollection) {
        String method = request.getMethod();
        url = StrUtil.isBlank(url) ? String.valueOf(request.getRequestURL()) : url;
        Map<String, String[]> parameterMap = Maps.newLinkedHashMap(request.getParameterMap());
        Map<String, Object> body = Maps.newLinkedHashMap();
        if (CollUtil.isEmpty(parameterMap)) {
            parameterMap = Maps.newLinkedHashMap();
        }
        CachedBodyHttpServletRequest cachedBodyHttpServletRequest = (CachedBodyHttpServletRequest) request;
        ServletInputStream requestInputStream = cachedBodyHttpServletRequest.getInputStream();
        String json = null;
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(requestInputStream))) {
            json = reader.lines().collect(Collectors.joining("\n"));
        }
        if (StrUtil.isNotBlank(json)) {
            Map<String, Object> readValue = JSON.parseObject(json, Map.class);
            readValue.entrySet().stream().forEach(o -> {
                String empty = null;
                Object value = (o.getValue() == null || "null".equals(o.getValue())) ? empty : o.getValue();
                body.put(o.getKey(), value);
            });

            //body.putAll(readValue);
        }


        log().info("请求参数 parameter:{}", parameterMap);
        log().info("请求参数 body:{}", body);
        String openFeign = request.getHeader(Openfeign.OPENFEIGN.getHeader());
        if (StrUtil.isNotBlank(openFeign)) {
            String host = request.getHeader("host");
            String[] urlSplit = url.split(host);
            url = new StringBuffer(urlSplit[0]).append(openFeign).append(urlSplit[urlSplit.length - 1]).toString();
            log().info("[{}] sign ...", Openfeign.OPENFEIGN.getDesc());
        }
        url = GatewayUtils.replaceUrl(request, url);

        String generalSign = generalSign(salt, method, url, parameterMap, body, exCollection);
        String headerSign = request.getHeader(signAsName);

        log().info("验签 {}:{}", signAsName, headerSign);
        log().info("验签 {}:{}", "generalSign", generalSign);
        if (!ObjectUtil.equals(generalSign, headerSign)) {
            throw new GlobalCustomException("[serviceName]==>签名不合法".replace("serviceName", serviceName));
        }
        return true;
    }

    /**
     * 1. 验证请求IP是否在白名单中
     *
     * @param ip
     * @param ipWhitelist
     * @return
     */

    default boolean verifyIpWhiteList(String ip, String ipWhitelist) {
        String temp = ipWhitelist;
        List<String> list = CollUtil.newArrayList();
        if (ObjectUtil.isNotEmpty(temp)) {
            list.addAll(Arrays.stream(temp.replace(" ", "").split(",")).collect(Collectors.toList()));
        }
        return list.contains(ip);
    }

    /**
     * 1. 验证请求IP是否在黑名单中
     *
     * @param ip
     * @param ipBlackList
     * @return
     */
    default boolean verifyIpBlackList(String ip, String ipBlackList) {
        String temp = ipBlackList;
        List<String> list = CollUtil.newArrayList();
        if (ObjectUtil.isNotEmpty(temp)) {
            list.addAll(Arrays.stream(temp.replace(" ", "").split(",")).collect(Collectors.toList()));
        }
        return list.contains(ip);
    }

    /**
     * @param salt         签名密钥
     * @param method       HTTP方法（GET、POST等）
     * @param url          请求的URL
     * @param params       请求参数的Map
     * @param body         请求body的Map
     * @param exCollection
     * @return 生成的签名字符串
     */
    @SneakyThrows
    default String generalSign(String salt, String method, String url, Map<String, String[]> params, Map<String, Object> body, Collection<String> exCollection) {
        return ApiUtil.generalSign(new SingleSignature()
                .setSalt(salt)
                .setMethod(method)
                .setUrl(url)
                .setParams(params)
                .setBody(body)
                .setExCollection(exCollection));
    }


}
