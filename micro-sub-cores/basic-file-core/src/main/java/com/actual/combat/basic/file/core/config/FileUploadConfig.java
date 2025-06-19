package com.actual.combat.basic.file.core.config;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.actual.combat.aop.abs.bean.AbsBean;
import com.actual.combat.basic.core.constant.file.FileConstant;
import com.actual.combat.basic.core.utils.file.FileUtils;
import com.actual.combat.basic.utils.object.ObjectUtils;
import com.actual.combat.nacos.utils.NaCosUtils;
import com.alibaba.cloud.nacos.NacosDiscoveryProperties;
import com.actual.combat.basic.file.core.utils.oss.LocalOSSUtils;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import jakarta.servlet.MultipartConfigElement;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.unit.DataSize;

import java.util.List;

/**
 * @Author yan
 * @Date 2025/3/9 1:57:11
 * @Description
 */
@AutoConfigureAfter(NacosDiscoveryProperties.class)
@Configuration
@ConditionalOnBean(RedisTemplate.class)
public class FileUploadConfig implements AbsBean {
    public static String instanceId;
    public static String fileControllerPath = "/file";
    public static String getPathKey = "config.file.url.byte.get";
    public static String delPathKey = "config.file.url.byte.del";
    public static String downLoadPathKey = "config.file.url.downLoad";
    public static int maxChunkSize = 1024 * 1024 * 1;
    @Resource
    private RedisTemplate redisTemplate;

    public static String getInstanceId() {
        if (StrUtil.isBlank(instanceId) || instanceId.endsWith("-1")) {
            instanceId = NaCosUtils.getInstanceId();
        }
        return instanceId;
    }

    @Bean
    public MultipartConfigElement multipartConfigElement() {
        Environment env = SpringUtil.getBean(Environment.class);
        Integer maxFileSize = env.getProperty("config.file.upload.maxFileSize", Integer.class, 10);
        Integer maxRequestSize = env.getProperty("config.file.upload.maxRequestSize", Integer.class, 1024);
        MultipartConfigFactory factory = new MultipartConfigFactory();
        // 设置单个文件的最大值
        factory.setMaxFileSize(DataSize.ofMegabytes(maxFileSize)); // 10MB
        // 设置总上传数据的最大值
        factory.setMaxRequestSize(DataSize.ofMegabytes(maxRequestSize)); // 10MB
        return factory.createMultipartConfig();
    }

    @Override
    @PostConstruct
    public void init() {
        AbsBean.super.init();
        List<String> fileNameList = LocalOSSUtils.getFileNameList();
        this.instanceId = NaCosUtils.getInstanceId();
        RedisTemplate redisTemplate = SpringUtil.getBean(RedisTemplate.class);
        String instanceId = getInstanceId();
        LocalOSSUtils.FILE_NAME_LIST.addAll(fileNameList);
        LocalOSSUtils.FILE_NAME_LIST.stream().forEach(fileName -> {
            if (FileUtils.isFile(fileName)) {
                redisTemplate.opsForHash().put(FileConstant.FILE_REDIS_FILE, fileName, instanceId);
            } else if (FileUtils.isDirectory(fileName)) {
                redisTemplate.opsForHash().put(FileConstant.FILE_REDIS_DIR + instanceId, fileName, "DIR");
                redisTemplate.opsForHash().put(FileConstant.FILE_REDIS_INSTANCE_ID, fileName, instanceId);
            }
        });
    }

    @Override
    @PreDestroy
    public void destroy() {
        //获取RedisTemplate实例
        RedisTemplate redisTemplate = SpringUtil.getBean(RedisTemplate.class);
        //删除指定目录下的文件
        //redisTemplate.opsForHash().delete(FileConstant.FILE_REDIS_DIR + getInstanceId());
        //遍历文件名列表
        LocalOSSUtils.FILE_NAME_LIST.stream().forEach(fileName -> {
            //如果文件存在
            if (FileUtils.isFile(fileName)) {
                //删除文件
                redisTemplate.opsForHash().delete(FileConstant.FILE_REDIS_FILE, fileName);
            //如果文件是目录
            } else if (FileUtils.isDirectory(fileName)) {
                //删除目录
                redisTemplate.opsForHash().delete(FileConstant.FILE_REDIS_DIR + instanceId, fileName);
                //删除实例ID
                redisTemplate.opsForHash().delete(FileConstant.FILE_REDIS_INSTANCE_ID, fileName);
            }
        });
        //调用父类的destroy方法
        AbsBean.super.destroy();
    }

    /**
     * 获取其他服务器文件字节 的url
     *
     * @param instanceId
     * @return
     */
    public static String getUrlByte(String instanceId) {
        return getUrl(instanceId, SpringUtil.getBean(Environment.class).getProperty(getPathKey), fileControllerPath);
    }

    /**
     * 获取其他服务器文件删除的url
     *
     * @param instanceId
     * @return
     */
    public static String getUrlDel(String instanceId) {
        return getUrl(instanceId, SpringUtil.getBean(Environment.class).getProperty(delPathKey), fileControllerPath);
    }

    /**
     * 获取其他服务器文件删除的url
     *
     * @param instanceId
     * @return
     */
    public static String getUrlDownLoad(String instanceId, String identifier) {
        return getUrl(instanceId, SpringUtil.getBean(Environment.class).getProperty(downLoadPathKey) + "/" + identifier, fileControllerPath);
    }

    public static String getUrl(String instanceId, String path, String controllerPath) {
        Environment environment = SpringUtil.getBean(Environment.class);
        String prefix = environment.getProperty("server.servlet.context-path", "");
        String serviceId = environment.getProperty("spring.application.name", "");
        prefix = prefix + controllerPath;
        return NaCosUtils.getUrl(serviceId, instanceId, prefix, path);
    }

    public static boolean isCurrentInstance(String instanceId) {
        return ObjectUtils.equals(instanceId, FileUploadConfig.instanceId);
    }
}
