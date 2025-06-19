package com.actual.combat.basic.file.core.storage.platform;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.common_code.config.OSConfig;
import com.minimalism.base.exception.BusinessException;
import com.minimalism.base.exception.GlobalConfigException;
import com.minimalism.file.domain.FileInfo;
import com.minimalism.file.domain.FilePart;
import com.actual.combat.basic.file.core.properties.FileProperties;
import com.actual.combat.basic.file.core.storage.StorageType;
import com.actual.combat.basic.file.core.storage.clientAbs.LocalClient;
import com.minimalism.base.utils.object.ObjectUtils;
import com.actual.combat.basic.file.core.utils.oss.LocalOSSUtils;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

/**
 * @Author yan
 * @Date 2025/3/7 20:44:58
 * @Description
 */

@Data
//@NoArgsConstructor
@AllArgsConstructor
public class LocalStorageClient implements LocalClient {
    private String directory;
    private String endPoint;
    private String nginxUrl;
    private String nginxDir;
    private String uploadDir;

    public void init(FileProperties.LocalProperties config) {
        try {
            String directory = config.getDirectory();
            String endPoint = config.getEndPoint();
            String nginxUrl = config.getNginxUrl();
            String uploadDir = config.getUploadDir();
            String nginxDir = config.getNginxDir();
            uploadDir = ObjectUtils.defaultIfEmpty(uploadDir, "tmp/upload");
            String separator = OSConfig.separator;
            if (!uploadDir.endsWith(separator)) {
                uploadDir = uploadDir + separator;
            }
            this.directory = directory;
            this.endPoint = endPoint;
            this.nginxUrl = nginxUrl;
            this.nginxDir = nginxDir;
            this.uploadDir = uploadDir;
        } catch (Exception e) {
            error("[Local] LocalStorage build failed: {}", e.getMessage());
            throw new GlobalConfigException("请检查本地存储配置是否正确");
        }
    }

    public LocalStorageClient() {
        FileProperties.LocalProperties config = SpringUtil.getBean(FileProperties.LocalProperties.class);
        init(config);
    }

    public LocalStorageClient(FileProperties.LocalProperties config) {
        init(config);
    }

    @Override
    public StorageType getType() {
        return StorageType.local;
    }

    @Override
    public boolean bucketExists(String bucket) {
        return FileUtil.newFile(bucket).exists();
    }

    @Override
    public void makeBucket(String bucket) {
        try {
            if (!bucketExists(directory)) {
                FileUtil.newFile(bucket).mkdirs();
            }
        } catch (Exception e) {
            error("[Local] makeBucket Exception:{}", e.getMessage());
            throw new GlobalConfigException("创建存储桶失败");
        }
    }

    @Override
    public void delete(String bucketName, String objectName) {
        LocalClient.super.delete(bucketName, objectName);
        if (StringUtils.isEmpty(objectName)) {
            throw new BusinessException("文件删除失败,文件路径为空");
        }
        try {
            Path file = Paths.get(getUploadDir()).resolve(Paths.get(objectName));
            FileUtil.del(file);
        } catch (Exception e) {
            error("[Local] file delete failed: {}", e.getMessage());
            throw new BusinessException("文件删除失败");
        }
    }

    @Override
    public FileInfo upload(String bucketName, String fileName, InputStream inputStream) {
        LocalClient.super.upload(bucketName, fileName, inputStream);
        String uploadUrl = LocalOSSUtils.upload(fileName, inputStream);
        Boolean aFalse = Boolean.FALSE;
        return buildFileInfo(fileName, FileUtil.getInputStream(uploadUrl), uploadUrl, Boolean.TRUE, aFalse, aFalse);
    }

    @Override
    public FileInfo uploadSharding(String bucketName, String fileName, InputStream inputStream, String identifier) {
        LocalClient.super.uploadSharding(bucketName, fileName, inputStream, identifier);

        if (StrUtil.isBlank(identifier)) {
            identifier = UUID.randomUUID().toString().replace("-", "") + "_" + fileName;
        }
        String bucketPath = LocalOSSUtils.getMergeFilePath(identifier, StrUtil.EMPTY);
        //String separator = OSType.getSeparator(null);
        //String bucketPath = uploadDir + separator + bucketName + separator + identifier + separator;
        //bucketPath = bucketPath.replace(separator + separator, separator);
        File bucketFile = FileUtil.newFile(bucketPath);
        if (!bucketFile.exists()) {
            bucketFile.mkdirs();
        }
        String fileLocal = LocalOSSUtils.uploadSharding(fileName, identifier, inputStream);

        Boolean aFalse = Boolean.FALSE;
        return buildFileInfo(fileName, FileUtil.getInputStream(fileLocal), fileLocal, Boolean.TRUE, aFalse, aFalse);
    }

    @Override
    public FileInfo uploadSharding(String bucketName, String fileName, InputStream inputStream, String identifier, String localPath) {
        LocalClient.super.uploadSharding(bucketName, fileName, inputStream, identifier);

        if (StrUtil.isBlank(identifier)) {
            identifier = UUID.randomUUID().toString().replace("-", "") + "_" + fileName;
        }
        String bucketPath = LocalOSSUtils.getMergeFilePath(identifier, StrUtil.EMPTY);
        //String separator = OSType.getSeparator(null);
        //String bucketPath = uploadDir + separator + bucketName + separator + identifier + separator;
        //bucketPath = bucketPath.replace(separator + separator, separator);
        File bucketFile = FileUtil.newFile(bucketPath);
        if (!bucketFile.exists()) {
            bucketFile.mkdirs();
        }
        String fileLocal;
        if (StrUtil.isNotBlank(localPath)) {
            fileLocal = localPath;
        } else {
            fileLocal = LocalOSSUtils.uploadSharding(fileName, identifier, inputStream);
        }
        Boolean aFalse = Boolean.FALSE;
        return buildFileInfo(fileName, FileUtil.getInputStream(fileLocal), fileLocal, Boolean.TRUE, aFalse, aFalse);
    }

    @Override
    public FilePart bulidFilePart(String identifier, int chunkNumber, String url, Boolean local, InputStream inputStream) {
        FilePart filePart = LocalClient.super.bulidFilePart(identifier, chunkNumber, url, local, inputStream);
        if (filePart.getLocal()) {
            filePart.setUploadDir(LocalOSSUtils.getUploadDir());
        }
        return filePart;
    }

    @Override
    public FilePart uploadShardingChunkNumber(String bucketName, int chunkNumber, String identifier, InputStream inputStream) {
        LocalClient.super.uploadShardingChunkNumber(bucketName, chunkNumber, identifier, inputStream);
        String partFileLocalUrl = LocalOSSUtils.splitChunkNumberFileLocal(chunkNumber, identifier, inputStream);
        return bulidFilePart(identifier, chunkNumber, partFileLocalUrl, Boolean.TRUE, FileUtil.getInputStream(partFileLocalUrl));
    }


    @Override
    public List<InputStream> getInputStreams(String bucketName, String identifier, int totalChunks) {
        List<InputStream> list = LocalOSSUtils.getSplitFileLocal(identifier, totalChunks);
        return list;
    }


    @Override
    public String getUrl(String bucketName, String objectName) {
        LocalClient.super.getUrl(bucketName, objectName);
        String url;
        String separator = OSConfig.separator;
        // 如果配置了nginxUrl则使用nginxUrl
        if (StrUtil.isNotBlank(nginxUrl)) {
            url = nginxUrl.endsWith(separator) ? nginxUrl + bucketName + separator + objectName : nginxUrl + separator + bucketName + separator + objectName;
        } else if (StrUtil.isBlank(endPoint)) {
            //表示直接使用服务器地址
            url = "";
        } else {
            url = endPoint + separator + LocalOSSUtils.getUploadDir() + separator + bucketName + separator + objectName;
        }
        url = url.replace(separator + separator, separator).replace(":" + separator, ":" + separator + separator);
        return url;
    }

}
