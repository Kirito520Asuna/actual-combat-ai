package com.actual_combat.nacos.utils;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.alibaba.cloud.nacos.discovery.NacosDiscoveryClient;
import org.springframework.cloud.client.ServiceInstance;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author yan
 * @Date 2025/2/7 11:36:35
 * @Description
 */
public class NaCosUtils {
    /**
     * @param serviceId  ==> 服务名
     * @param instanceId ==> ip:port
     * @param prefix     ==> 前缀
     * @param path       ==> 路径
     * @return
     */
    public static String getUrl(String serviceId, String instanceId, String prefix, String path) {
        String instanceIdFormat = "%s:%s";
        String urlFormat = "http://" + instanceIdFormat + "/" + prefix + "/" + path;
        urlFormat = urlFormat.replace("//", "/").replace("//", "/").replace(":/", "://");
        String url = null;
        NacosDiscoveryClient discoveryClient = SpringUtil.getBean(NacosDiscoveryClient.class);

        List<ServiceInstance> serviceInstances = discoveryClient.getInstances(serviceId);
        ServiceInstance serviceInstance = serviceInstances.stream()
                .filter(instance -> ObjectUtil.isNotEmpty(instanceId) && ObjectUtil.equals(instanceId, String.format(instanceIdFormat, instance.getHost(), instance.getPort())))
                .findFirst().orElse(null);
        if (serviceInstance != null) {
            String host = serviceInstance.getHost();
            int port = serviceInstance.getPort();
            url = String.format(urlFormat, host, port);
        } else {
            throw new RuntimeException("服务未找到,请检查服务名是否正确或实例是否在线,服务名:" + serviceId + ",实例id:" + instanceId);
        }
        return url;
    }

    /**
     * 获取当前实例id
     *
     * @return
     */

    public static String getInstanceId() {
        //NacosDiscoveryClient bean = SpringUtil.getBean(NacosDiscoveryClient.class);
        NacosDiscoveryProperties nacosDiscoveryProperties = SpringUtil.getBean(NacosDiscoveryProperties.class);
        return nacosDiscoveryProperties.getIp() + ":" + nacosDiscoveryProperties.getPort();
    }

    /**
     * 获取所有实例id
     *
     * @param serviceId
     * @return
     */
    public static List<String> getInstanceIds(String serviceId) {
        return getInstanceIds(null, serviceId);
    }

    /**
     * 获取所有实例id
     *
     * @param nacosDiscoveryClient
     * @param serviceId
     * @return
     */
    public static List<String> getInstanceIds(NacosDiscoveryClient nacosDiscoveryClient, String serviceId) {
        if (nacosDiscoveryClient == null) {
            nacosDiscoveryClient = SpringUtil.getBean(NacosDiscoveryClient.class);
        }

        List<String> instanceIds = nacosDiscoveryClient.getInstances(serviceId).stream().map(instance -> {
            String host = instance.getHost();
            int port = instance.getPort();
            return host + ":" + port;
        }).collect(Collectors.toList());
        return instanceIds;
    }

    /**
     * 获取所有实例id
     *
     * @return
     */
    public static List<String> getInstanceIds() {
        List<String> instanceIds = CollUtil.newArrayList();
        NacosDiscoveryClient nacosDiscoveryClient = SpringUtil.getBean(NacosDiscoveryClient.class);
        nacosDiscoveryClient.getServices().stream().forEach(serviceId -> {
            instanceIds.addAll(getInstanceIds(nacosDiscoveryClient, serviceId));
        });

        return instanceIds;
    }

    /**
     * 判断实例id是否存在
     *
     * @param instanceId
     * @return
     */

    public static boolean existInstanceId(String instanceId) {
        return getInstanceIds().contains(instanceId);
    }

    /**
     * 判断实例id是否存在
     *
     * @param serviceId
     * @param instanceId
     * @return
     */
    public static boolean existInstanceId(String serviceId, String instanceId) {
        return getInstanceIds(null, serviceId).contains(instanceId);
    }

}
