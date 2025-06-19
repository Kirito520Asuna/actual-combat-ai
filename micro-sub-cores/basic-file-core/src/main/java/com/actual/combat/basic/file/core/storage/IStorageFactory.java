package com.actual.combat.basic.file.core.storage;

import cn.hutool.extra.spring.SpringUtil;
import com.actual.combat.aop.abs.bean.AbsBean;
import com.actual.combat.basic.exceptions.BusinessException;
import com.actual.combat.basic.file.core.properties.FileProperties;
import com.actual.combat.basic.file.core.storage.platform.AliyunOssStorageClient;
import com.actual.combat.basic.file.core.storage.platform.LocalStorageClient;
import com.actual.combat.basic.file.core.storage.platform.MinioStorageClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * @Author yan
 * @Date 2025/3/7 19:57:48
 * @Description
 */
@Component
//@Deprecated
@RequiredArgsConstructor
public class IStorageFactory implements IFileStorageProvider, AbsBean {
    @Override
    public void init() {
        log().debug("[init]-[Factory] {}", getAClassName());
    }

    @Override
    public IFileStorageClient getStorage() {
        FileProperties fileProperties = SpringUtil.getBean(FileProperties.class);
        StorageType type = fileProperties.getType();
        IFileStorageClient fileStorage;
        switch (type) {
            case aliyunOSS:
                fileStorage = new AliyunOssStorageClient(fileProperties.getAliyunOss());
                break;
            case local:
                fileStorage = new LocalStorageClient(fileProperties.getLocal());
                break;
            case minio:
                fileStorage = new MinioStorageClient(fileProperties.getMinio());
                break;
            default:
                throw new BusinessException("不支持的存储平台");
        }
        return fileStorage;
    }
}
