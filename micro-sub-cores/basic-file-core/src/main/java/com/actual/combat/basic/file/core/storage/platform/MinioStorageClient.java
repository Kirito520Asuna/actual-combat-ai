package com.actual.combat.basic.file.core.storage.platform;

import cn.hutool.core.util.StrUtil;
import com.actual.combat.basic.constant.Constants;
import com.actual.combat.basic.core.config.os.OSConfig;
import com.actual.combat.basic.core.utils.io.IoUtils;
import com.actual.combat.basic.enums.OSType;
import com.actual.combat.basic.exceptions.GlobalConfigException;
import com.actual.combat.basic.file.core.properties.FileProperties;
import com.actual.combat.basic.file.core.storage.StorageType;
import com.actual.combat.basic.file.core.utils.oss.MinioOSSUtils;
import com.actual.combat.file.domain.FileInfo;
import com.actual.combat.file.domain.FilePart;
import io.minio.ListObjectsArgs;
import io.minio.MinioClient;
import io.minio.Result;
import io.minio.messages.Item;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


/**
 * Minio文件上传
 *
 * @Author: hao.ding@insentek.com
 * @Date: 2024/1/25 10:05
 */
@Slf4j
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MinioStorageClient implements com.actual.combat.basic.file.core.storage.clientAbs.MinioClient {

    private MinioClient minioClient;
    private String endPoint;
    private String bucket;

    public void init(FileProperties.MinioProperties config) {
        try {
            String accessKey = config.getAccessKey();
            String secretKey = config.getSecretKey();
            String endPoint = config.getEndpoint();
            String bucket = config.getBucket();
            this.minioClient = MinioOSSUtils.createMinioClient(accessKey, secretKey, endPoint);
            this.endPoint = endPoint;
            this.bucket = bucket;
        } catch (Exception e) {
            log.error("[Minio] MinioClient build failed: {}", e.getMessage());
            throw new GlobalConfigException("请检查Minio配置是否正确");
        }
    }

    public MinioStorageClient(FileProperties.MinioProperties config) {
        init(config);
    }

    @Override
    public StorageType getType() {
        return StorageType.minio;
    }

    @Override
    public boolean bucketExists(String bucket) {
        return MinioOSSUtils.doesBucketExist(minioClient, bucket);
    }

    @Override
    public void makeBucket(String bucket) {
        MinioOSSUtils.createBucket(minioClient, bucket);
    }

    @Override
    public void delete(String bucketName, String objectName) {
        MinioOSSUtils.deleteMinio(minioClient, bucketName, objectName);
    }

    @SneakyThrows
    @Override
    public void download(String bucketName, String objectName, HttpServletResponse response) {
        if (StrUtil.isBlank(bucketName)) {
            bucketName = getBucket();
        }
        InputStream inputStream = MinioOSSUtils.downloadMinioInput(minioClient, bucketName, objectName);
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
        com.actual.combat.basic.file.core.storage.clientAbs.MinioClient.super.upload(bucketName, fileName, inputStream);
        if (StrUtil.isBlank(bucketName)) {
            bucketName = getBucket();
        }
        String url = MinioOSSUtils.uploadMinio(minioClient, bucketName, fileName, inputStream);
        Boolean aFalse = Boolean.FALSE;
        return buildFileInfo(fileName, inputStream, url, aFalse, aFalse, aFalse);
    }

    @SneakyThrows
    @Override
    public FileInfo uploadSharding(String bucketName, String fileName, InputStream inputStream, String identifier) {
        com.actual.combat.basic.file.core.storage.clientAbs.MinioClient.super.uploadSharding(bucketName, fileName, inputStream, identifier);
        if (StrUtil.isBlank(bucketName)) {
            bucketName = getBucket();
        }
        String url = MinioOSSUtils.uploadShardingOss(minioClient, bucketName, fileName, inputStream);

        Boolean aFalse = Boolean.FALSE;
        return buildFileInfo(fileName, inputStream, url, aFalse, aFalse, aFalse);
    }



    @Override
    public FilePart uploadShardingChunkNumber(String bucketName, int chunkNumber, String identifier, InputStream inputStream) {
        String separator = OSConfig.getSeparator(OSType.linux.name());
        String fileName = identifier + separator + chunkNumber + Constants.PART_SUFFIX;
        FileInfo fileInfo = uploadSharding(bucketName, fileName, inputStream, identifier);
        String url = fileInfo.getUrl();
        return FilePart.builder()
                .fileId(fileInfo.getFileId())
                .partSize(fileInfo.getSize())
                .url(url)
                .partSort(chunkNumber)
                .partCode(identifier)
                .local(Boolean.FALSE)
                .build();
    }

    @Override
    public FileInfo uploadMergeChunks(InputStream inputStream, String fileMainName, String identifier) {
        if (identifier.endsWith("/")) {
            identifier = identifier + "/";
        }
        Iterable<Result<Item>> results = minioClient.listObjects(
                ListObjectsArgs.builder()
                        .bucket(getBucket())
                        .prefix(identifier)  // 注意：必须以“/”结尾
                        .recursive(false)     // 非递归只查询该文件夹下的内容
                        .build());
        for (Result<Item> result : results) {
            try {
                Item item = result.get();
                System.out.println("文件名：" + item.objectName() + ", 大小：" + item.size());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}

