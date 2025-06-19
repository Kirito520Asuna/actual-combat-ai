package com.actual.combat.basic.file.core.storage.clientAbs;

import cn.hutool.core.collection.CollUtil;
import com.actual.combat.basic.file.core.storage.IFileStorageClient;
import com.actual.combat.basic.file.core.storage.StorageType;
import lombok.SneakyThrows;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * @Author yan
 * @Date 2025/3/14 2:17:21
 * @Description
 */
public interface MinioClient extends IFileStorageClient {
    List<StorageType> minioClientList = CollUtil.newArrayList(StorageType.minio);

    @Override
    default boolean support(StorageType storageType) {
        return minioClientList.contains(storageType);
    }

    @SneakyThrows
    void download(String bucketName, String objectName, HttpServletResponse response);
}
