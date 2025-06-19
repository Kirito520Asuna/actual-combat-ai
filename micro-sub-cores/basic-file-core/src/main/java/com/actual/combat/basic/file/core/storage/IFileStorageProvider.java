package com.actual.combat.basic.file.core.storage;

/**
 * 文件上传对象工厂
 * @Author yan
 * @Date 2025/3/7 18:58:00
 * @Description
 */
public interface IFileStorageProvider {
    IFileStorageClient getStorage();
}
