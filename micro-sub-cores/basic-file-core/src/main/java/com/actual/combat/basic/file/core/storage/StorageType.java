package com.actual.combat.basic.file.core.storage;

/**
 * 存储平台类型
 * @Author yan
 * @Date 2025/3/7 下午5:59:53
 * @Description
 */
public enum StorageType {

    /**
     * 本地
     */
    local,
    /**
     * 阿里云OSS
     */
    aliyunOSS,
    /**
     * 七牛云
     */
    qiniu,
    /**
     * MinIO
     */
    minio;
}