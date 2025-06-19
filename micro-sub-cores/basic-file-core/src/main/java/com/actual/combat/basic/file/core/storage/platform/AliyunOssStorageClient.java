package com.actual.combat.basic.file.core.storage.platform;

import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.actual.combat.basic.core.utils.io.IoUtils;
import com.actual.combat.basic.exceptions.GlobalConfigException;
import com.actual.combat.file.domain.FileInfo;
import com.aliyun.oss.OSS;
import com.actual.combat.basic.file.core.properties.FileProperties;
import com.actual.combat.basic.file.core.storage.StorageType;
import com.actual.combat.basic.file.core.storage.clientAbs.AliyunClient;
import com.actual.combat.basic.file.core.utils.oss.AliyunOSSUtils;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.SneakyThrows;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


/**
 * @Author yan
 * @Date 2025/3/7 19:24:20
 * @Description
 */
@Data
@AllArgsConstructor
public class AliyunOssStorageClient implements AliyunClient {
    private OSS client;
    private String endPoint;
    private String bucket;

    public void init(FileProperties.AliyunOssProperties config) {
        try {
            String accessKey = config.getAccessKey();
            String secretKey = config.getSecretKey();
            String endPoint = config.getEndpoint();
            String bucket = config.getBucket();
            this.client = AliyunOSSUtils.createOss(accessKey, secretKey, endPoint);
            this.endPoint = endPoint;
            this.bucket = bucket;
        } catch (Exception e) {
            log().error("[AliyunOSS] OSSClient build failed: {}", e.getMessage());
            throw new GlobalConfigException("请检查阿里云OSS配置是否正确");
        }
    }

    public AliyunOssStorageClient() {
        FileProperties.AliyunOssProperties config;
        try {
            config = SpringUtil.getBean(FileProperties.AliyunOssProperties.class);
        } catch (Exception e) {
            config = SpringUtil.getBean(FileProperties.AliyunOssProperties.class);
        }

        init(config);
    }


    public AliyunOssStorageClient(FileProperties.AliyunOssProperties config) {
        init(config);
    }

    @Override
    public StorageType getType() {
        return StorageType.aliyunOSS;
    }

    @Override
    public boolean bucketExists(String bucket) {
        return AliyunOSSUtils.doesBucketExist(client, bucket);
    }

    @Override
    public void makeBucket(String bucket) {
        AliyunOSSUtils.createBucketName(client, bucket);
    }

    @Override
    public void delete(String bucketName, String objectName) {
        if (StrUtil.isBlank(bucketName)) {
            bucketName = bucket;
        }
        AliyunOSSUtils.deleteOss(client, bucketName, objectName);
    }

    @SneakyThrows
    @Override
    public void download(String bucketName, String objectName, HttpServletResponse response) {
        if (StrUtil.isBlank(bucketName)) {
            bucketName = getBucket();
        }
        InputStream inputStream = AliyunOSSUtils.downloadOssInput(client, bucketName, objectName);
        OutputStream responseOutputStream = response.getOutputStream();
        // 将 OSS 输出流的内容复制到 HTTP 响应输出流中
        IoUtils.copy(inputStream, responseOutputStream);
        // 设置文件ContentType类型，这样设置，会自动判断下载文件类型
        response.setContentType("application/x-msdownload");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setHeader("Content-Disposition", "attachment;fileName=" + URLEncoder.encode(objectName, String.valueOf(StandardCharsets.UTF_8)));
        response.flushBuffer();
        IoUtils.close(responseOutputStream);
    }

    @Override
    public FileInfo upload(String bucketName, String fileName, InputStream inputStream) {
        AliyunClient.super.upload(bucketName, fileName, inputStream);
        String url = AliyunOSSUtils.uploadOss(client, bucketName, fileName, inputStream);
        Boolean aFalse = Boolean.FALSE;
        return buildFileInfo(fileName, inputStream, url, aFalse, aFalse, aFalse);
    }

    @SneakyThrows
    @Override
    public FileInfo uploadSharding(String bucketName, String fileName, InputStream inputStream, String identifier) {
        AliyunClient.super.uploadSharding(bucketName, fileName, inputStream, identifier);
        String url = AliyunOSSUtils.uploadShardingOss(client, bucketName, fileName, inputStream);

        Boolean aFalse = Boolean.FALSE;
        return buildFileInfo(fileName, inputStream, url, aFalse, aFalse, aFalse);
    }

}
