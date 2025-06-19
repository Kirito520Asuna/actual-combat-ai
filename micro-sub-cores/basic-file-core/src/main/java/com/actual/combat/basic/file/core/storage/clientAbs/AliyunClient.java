package com.actual.combat.basic.file.core.storage.clientAbs;

import cn.hutool.core.collection.CollUtil;
import com.actual.combat.basic.file.core.storage.IFileStorageClient;
import com.actual.combat.basic.file.core.storage.StorageType;

import java.util.List;

/**
 * @Author yan
 * @Date 2025/3/8 16:40:36
 * @Description
 */
public interface AliyunClient extends IFileStorageClient {
    List<StorageType> aliyunClientList = CollUtil.newArrayList(StorageType.aliyunOSS);

    @Override
    default boolean support(StorageType storageType) {
        return aliyunClientList.contains(storageType);
    }
}
