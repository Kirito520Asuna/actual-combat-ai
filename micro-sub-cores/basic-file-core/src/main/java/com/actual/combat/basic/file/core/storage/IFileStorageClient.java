package com.actual.combat.basic.file.core.storage;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.actual.combat.aop.abs.bean.AbsBean;
import com.actual.combat.basic.core.utils.io.IoUtils;
import com.actual.combat.basic.file.core.utils.oss.LocalOSSUtils;
import com.actual.combat.basic.utils.object.ObjectUtils;
import com.actual.combat.file.domain.FileInfo;
import com.actual.combat.file.domain.FilePart;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * @Author yan
 * @Date 2025/3/7 18:58:37
 * @Description
 */
public interface IFileStorageClient extends AbsBean {
    default boolean support(StorageType storageType) {
        return false;
    }

    default StorageType getType() {
        return StorageType.local;
    }

    default String getBucket() {
        return null;
    }

    default String getEndPoint() {
        return null;
    }

    /**
     * 判断存储桶是否在存在
     *
     * @param bucket 桶名称
     * @return true 存在 false 不存在
     */
    boolean bucketExists(String bucket);

    /**
     * 创建存储桶
     *
     * @param bucket 桶名称
     */
    void makeBucket(String bucket);

    /**
     * 删除文件
     *
     * @param objectName 对象名称
     */
    default void delete(String objectName) {
        delete(null, objectName);
    }

    /**
     * 删除文件
     *
     * @param bucketName
     * @param objectName
     */
    default void delete(String bucketName, String objectName) {
        if (StrUtil.isBlank(bucketName)) {
            bucketName = getBucket();
        }
    }

    /**
     * 下载文件
     *
     * @param objectName 对象名称
     * @param response
     */
    default void download(String objectName, HttpServletResponse response) {
        download(null, objectName, response);
    }

    /**
     * 下载文件
     *
     * @param bucketName
     * @param objectName
     * @param response
     */
    default void download(String bucketName, String objectName, HttpServletResponse response) {
        if (StrUtil.isBlank(bucketName)) {
            bucketName = getBucket();
        }
    }

    /**
     * 构建文件信息
     *
     * @param fileName
     * @param inputStream
     * @param url
     * @param dir
     * @param img
     * @return
     */
    default FileInfo buildFileInfo(String fileName, InputStream inputStream, String url, Boolean local, Boolean dir, Boolean img) {
        String mainName = FileUtil.mainName(fileName);
        String type = FileUtil.extName(fileName);
        long size = IoUtils.size(inputStream);

        return new FileInfo()
                .setUrl(url)
                .setDir(dir)
                .setFileName(mainName)
                .setImg(img)
                .setLocal(ObjectUtils.defaultIfEmpty(local, Boolean.FALSE))
                .setSuffix("." + type)
                .setType(type)
                .setName(mainName)
                .setSize(size);
    }

    /**
     * 构建文件分片信息
     *
     * @param identifier
     * @param chunkNumber
     * @param url
     * @param local
     * @param inputStream
     * @return
     */
    default FilePart bulidFilePart(String identifier, int chunkNumber, String url, Boolean local, InputStream inputStream) {
        local = ObjectUtils.defaultIfEmpty(local, Boolean.FALSE);
        return new FilePart()
                .setUrl(local ? null : url)
                .setMergeDelete(Boolean.FALSE)
                .setLocalResource(local ? url : null)
                .setPartSize(IoUtils.size(inputStream))
                .setPartSort(chunkNumber)
                .setPartCode(identifier)
                .setLocal(local);
    }

    /**
     * 上传文件
     *
     * @param fileName
     * @param inputStream
     */
    default FileInfo upload(String fileName, InputStream inputStream) {
        return upload(null, fileName, inputStream);
    }


    /**
     * 上传文件
     *
     * @param bucketName  桶名称
     * @param fileName    文件名称
     * @param inputStream
     */
    default FileInfo upload(String bucketName, String fileName, InputStream inputStream) {
        if (StrUtil.isBlank(bucketName)) {
            bucketName = getBucket();
        }
        return null;
    }

    default FileInfo uploadSharding(String fileName, InputStream inputStream, String identifier) {
        return uploadSharding(null, fileName, inputStream, identifier);
    }

    /**
     * 上传文件 分片
     *
     * @param bucketName
     * @param fileName
     * @param inputStream
     */
    default FileInfo uploadSharding(String bucketName, String fileName, InputStream inputStream, String identifier) {
        if (StrUtil.isBlank(bucketName)) {
            bucketName = getBucket();
        }
        return null;
    }
    default FileInfo uploadSharding(String fileName, InputStream inputStream, String identifier, String localPath) {
        return uploadSharding(null, fileName, inputStream, identifier,localPath);
    }
   @SneakyThrows
   default FileInfo uploadSharding(String bucketName, String fileName, InputStream inputStream, String identifier, String localPath){
       if (StrUtil.isNotBlank(localPath)) {
           inputStream = new FileInputStream(localPath);
        }
       return uploadSharding(bucketName, fileName, inputStream, identifier);
   }


    /**
     * 上传单个分片文件
     *
     * @param chunkNumber
     * @param identifier
     * @param inputStream
     * @return
     */
    default FilePart uploadShardingChunkNumber(int chunkNumber, String identifier, InputStream inputStream) {
        return uploadShardingChunkNumber(null, chunkNumber, identifier, inputStream);
    }

    /**
     * 上传单个分片文件
     *
     * @param bucketName
     * @param chunkNumber
     * @param identifier
     * @param inputStream
     * @return
     */
    default FilePart uploadShardingChunkNumber(String bucketName, int chunkNumber, String identifier, InputStream inputStream) {
        if (StrUtil.isBlank(bucketName)) {
            bucketName = getBucket();
        }
        return null;
    }

    /**
     * 获取url
     *
     * @param objectName
     * @return
     */
    default String getUrl(String objectName) {
        return getUrl(null, objectName);
    }

    /**
     * 获取url
     *
     * @param bucketName
     * @param objectName
     * @return
     */
    default String getUrl(String bucketName, String objectName) {
        if (StrUtil.isBlank(bucketName)) {
            bucketName = getBucket();
        }
        String url = "https://" + bucketName + "." + getEndPoint() + "/" + objectName;
        return url.replace("//", "/").replace(":/", "://");
    }

    /**
     * 获取 {@link List<InputStream>}
     *
     * @param identifier
     * @param totalChunks
     * @return
     */
    default List<InputStream> getInputStreams(String identifier, int totalChunks) {
        return getInputStreams(null, identifier, totalChunks);
    }

    /**
     * 获取 {@link List<InputStream>}
     *
     * @param bucketName
     * @param identifier
     * @param totalChunks
     * @return
     */
    default List<InputStream> getInputStreams(String bucketName, String identifier, int totalChunks) {
        if (StrUtil.isBlank(bucketName)) {
            bucketName = getBucket();
        }
        return null;
    }

    /**
     * 合并分片文件
     *
     * @param inputStream
     * @param fileMainName
     * @param identifier
     * @return
     */
    default FileInfo uploadMergeChunks(InputStream inputStream, String fileMainName, String identifier) {
        return uploadSharding(fileMainName, inputStream, identifier);
    }

    /**
     * 获取本地合并文件路径
     *
     * @param identifier
     * @param fileMainName
     * @return
     */
    default String getMergeFilePath(String identifier, String fileMainName) {
        return LocalOSSUtils.getMergeFilePath(identifier, fileMainName);
    }

    /**
     * 获取本地分片文件目录路径
     *
     * @param identifier
     * @return
     */
    default String getChunkDirPath(String identifier) {
        return LocalOSSUtils.getChunkDirPath(identifier);
    }
}
