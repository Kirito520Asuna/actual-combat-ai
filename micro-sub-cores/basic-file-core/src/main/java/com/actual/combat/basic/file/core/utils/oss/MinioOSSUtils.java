package com.actual.combat.basic.file.core.utils.oss;

import com.aliyun.oss.OSS;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import io.minio.messages.Bucket;
import io.minio.messages.Item;
import io.minio.messages.Part;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URL;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * MinioOSSUtils 模仿 AliyunOSSUtils 实现，基于 MinIO Java SDK
 *
 * @author yan
 * @date 2023/6/27
 */
public class MinioOSSUtils {

    /**
     * 创建 MinIO 客户端实例
     *
     * @param endpoint      MinIO 服务地址，例如 "http://localhost:9000"
     * @param accessKey     访问密钥
     * @param secretKey     密钥
     * @return MinioClient 实例
     */
    public static MinioClient createMinioClient(String endpoint, String accessKey, String secretKey) {
        return MinioClient.builder()
                .endpoint(endpoint)
                .credentials(accessKey, secretKey)
                .build();
    }

    /**
     * 判断桶是否存在
     *
     * @param minioClient MinioClient 实例
     * @param bucketName  桶名
     * @return true 存在，false 不存在
     */
    @SneakyThrows
    public static boolean doesBucketExist(MinioClient minioClient, String bucketName) {
        return minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build());
    }

    /**
     * 创建桶，如果桶不存在则创建
     *
     * @param minioClient MinioClient 实例
     * @param bucketName  桶名
     * @return 创建的 Bucket 信息，若已存在则返回 null
     */
    @SneakyThrows
    public static Bucket createBucket(MinioClient minioClient, String bucketName) {
        if (!doesBucketExist(minioClient, bucketName)) {
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            // 获取桶信息
            List<Bucket> buckets = minioClient.listBuckets();
            for (Bucket bucket : buckets) {
                if (bucket.name().equals(bucketName)) {
                    return bucket;
                }
            }
        }
        return null;
    }

    /**
     * 列出桶中的所有对象
     *
     * @param minioClient MinioClient 实例
     * @param bucketName  桶名
     * @return 对象列表
     */
    @SneakyThrows
    public static List<Item> listBucketAllObjects(MinioClient minioClient, String bucketName) {
        List<Item> items = new ArrayList<>();
        Iterable<Result<Item>> results = minioClient.listObjects(ListObjectsArgs.builder().bucket(bucketName).build());
        for (Result<Item> result : results) {
            items.add(result.get());
        }
        return items;
    }

    /**
     * 普通上传文件，并生成无时间限制的 URL（去除参数部分）
     *
     * @param minioClient MinioClient 实例
     * @param bucketName  桶名
     * @param objectName  对象名
     * @param inputStream 文件输入流
     * @return 上传后生成的 URL
     */
    @SneakyThrows
    public static String uploadMinio(MinioClient minioClient, String bucketName, String objectName, InputStream inputStream) {
        // 上传对象
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .stream(inputStream, -1, 10485760) // -1 表示未知流大小，最大10MB内存缓存
                .build());
        // 生成预签名URL，有效期设为1天
        String url = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(bucketName)
                .object(objectName)
                .expiry((int) Duration.ofDays(1).getSeconds())
                .build());
        // 去除 URL 中的查询参数
        int idx = url.indexOf("?");
        if (idx > 0) {
            url = url.substring(0, idx);
        }
        return url;
    }

    /**
     * 分片上传文件，并生成无时间限制的 URL（去除参数部分）
     * @param minioClient
     * @param bucketName
     * @param objectName
     * @param inputStream
     * @return
     */
    @SneakyThrows
    public static String uploadShardingOss(MinioClient minioClient, String bucketName, String objectName, InputStream inputStream) {
        final long partSize = 10 * 1024 * 1024L;
        // 上传对象
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .stream(inputStream, -1, partSize) // -1 表示未知流大小，最大10MB内存缓存
                .build());
        // 生成预签名URL，有效期设为1天
        String url = minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                .method(Method.GET)
                .bucket(bucketName)
                .object(objectName)
                .expiry((int) Duration.ofDays(1).getSeconds())
                .build());
        int idx = url.indexOf("?");
        if (idx > 0) {
            url = url.substring(0, idx);
        }
        return url;
    }
    /**
     * 生成随机对象键
     *
     * @param minioClient MinioClient 实例（用于检测是否重复）
     * @param bucketName  桶名
     * @param extension   后缀名，例如 ".png"
     * @return 随机生成的对象键
     */
    public static String generateKey(MinioClient minioClient, String bucketName, String extension) {
        String key = UUID.randomUUID().toString() + (extension != null ? extension : "*");
        try {
            // 尝试获取对象来判断是否存在
            minioClient.statObject(StatObjectArgs.builder().bucket(bucketName).object(key).build());
            // 若存在则递归重新生成
            return generateKey(minioClient, bucketName, extension);
        } catch (Exception e) {
            // 抛出异常表示不存在
            return key;
        }
    }




    /**
     * 下载文件，返回字节输出流
     *
     * @param minioClient MinioClient 实例
     * @param bucketName  桶名
     * @param objectName  对象名
     * @return ByteArrayOutputStream 对象
     */
    @SneakyThrows
    public static ByteArrayOutputStream downloadMinioOut(MinioClient minioClient, String bucketName, String objectName) {
        InputStream stream = downloadMinioInput(minioClient, bucketName, objectName);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        IOUtils.copy(stream, outputStream);
        stream.close();
        return outputStream;
    }

    /**
     * 下载文件，返回输入流
     *
     * @param minioClient MinioClient 实例
     * @param bucketName  桶名
     * @param objectName  对象名
     * @return InputStream 对象
     */
    @SneakyThrows
    public static InputStream downloadMinioInput(MinioClient minioClient, String bucketName, String objectName) {
        return minioClient.getObject(GetObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build());
    }

    /**
     * 删除对象
     *
     * @param minioClient MinioClient 实例
     * @param bucketName  桶名
     * @param objectName  对象名
     */
    @SneakyThrows
    public static void deleteMinio(MinioClient minioClient, String bucketName, String objectName) {
        minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .build());
    }

}
