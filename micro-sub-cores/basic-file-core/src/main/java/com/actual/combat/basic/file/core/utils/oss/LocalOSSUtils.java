package com.actual.combat.basic.file.core.utils.oss;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.minimalism.common_code.config.OSConfig;
import com.minimalism.base.constant.Constants;
import com.minimalism.base.constant.file.FileConstant;
import com.minimalism.base.exception.GlobalCustomException;
import com.minimalism.common_code.utils.file.FileUtils;
import com.actual.combat.basic.file.core.config.FileUploadConfig;
import com.minimalism.file.domain.FilePart;
import com.actual.combat.basic.file.core.properties.FileProperties;
import com.minimalism.common_code.utils.io.IoUtils;
import com.minimalism.base.utils.object.ObjectUtils;
import lombok.SneakyThrows;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * @Author yan
 * @Date 2025/3/7 23:35:41
 * @Description
 */
public class LocalOSSUtils {
    // 本地存储当前实例的文件地址（线程安全）
    public static final Set<String> FILE_NAME_LIST = ConcurrentHashMap.newKeySet();

    public static String getRedisInstanceId(File file) {
        return getRedisInstanceId(FileUtils.getAbsolutePath(file));
    }

    public static String getRedisInstanceId(String filePath) {
        RedisTemplate redisTemplate = SpringUtil.getBean(RedisTemplate.class);
        String instanceId = null;
        if (FileUtils.isDirectory(filePath)){
            instanceId = (String) redisTemplate
                    .opsForHash().get(FileConstant.FILE_REDIS_INSTANCE_ID, filePath);
        }else if (FileUtils.isFile(filePath)){
            instanceId = (String) redisTemplate
                    .opsForHash().get(FileConstant.FILE_REDIS_FILE, filePath);
        }
        return instanceId;
    }

    public static void putRedisFile(File file) {
        putRedisFile(FileUtils.getAbsolutePath(file));
    }

    public static void putRedisFile(String filePath) {
        String instanceId = getRedisInstanceId(filePath);
        String currentInstanceId = FileUploadConfig.getInstanceId();
        if (!ObjectUtils.equals(currentInstanceId, instanceId)) {
            RedisTemplate redisTemplate = SpringUtil.getBean(RedisTemplate.class);
            if (FileUtils.isDirectory(filePath)) {
                if (!filePath.endsWith(OSConfig.separator)) {
                    filePath = filePath + OSConfig.separator;
                }
                redisTemplate.opsForHash().put(FileConstant.FILE_REDIS_DIR + currentInstanceId, filePath, "DIR");
                redisTemplate.opsForHash().put(FileConstant.FILE_REDIS_INSTANCE_ID, filePath, currentInstanceId);
            }

            if (FileUtils.isFile(filePath)) {
                redisTemplate.opsForHash().put(FileConstant.FILE_REDIS_FILE, filePath, currentInstanceId);
            }
            FILE_NAME_LIST.add(filePath);
        }
    }

    public static void delRedisFile(File file) {
        delRedisFile(FileUtils.getAbsolutePath(file));
    }

    public static void delRedisFile(String filePath) {
        String instanceId = getRedisInstanceId(filePath);
        String currentInstanceId = FileUploadConfig.getInstanceId();
        if (ObjectUtils.equals(currentInstanceId, instanceId)) {
            RedisTemplate redisTemplate = SpringUtil.getBean(RedisTemplate.class);
            if (FileUtils.isDirectory(filePath)) {
                redisTemplate.opsForHash().delete(FileConstant.FILE_REDIS_DIR + currentInstanceId, filePath);
                redisTemplate.opsForHash().delete(FileConstant.FILE_REDIS_INSTANCE_ID, filePath);
            }

            if (FileUtils.isFile(filePath)) {
                String dir = StrUtil.subBefore(filePath, FileUtils.getName(filePath), true);
                instanceId = (String) redisTemplate.opsForHash().get(FileConstant.FILE_REDIS_INSTANCE_ID, dir);
                if (ObjectUtils.isNotEmpty(instanceId)) {
                    redisTemplate.opsForHash().delete(FileConstant.FILE_REDIS_INSTANCE_ID, dir);
                    redisTemplate.opsForHash().delete(FileConstant.FILE_REDIS_DIR + instanceId, dir);
                }
                redisTemplate.opsForHash().delete(FileConstant.FILE_REDIS_FILE, filePath);
            }
            FILE_NAME_LIST.remove(filePath);
        }
    }

    /**
     * 获取上传目录
     *
     * @return
     */
    public static String getUploadDir() {
        String separator = OSConfig.getSeparator();
        String uploadDir = separator;
        try {
            FileProperties fileProperties = SpringUtil.getBean(FileProperties.class);
            FileProperties.LocalProperties local = fileProperties.getLocal();
            uploadDir = local.getUploadDir();
        } catch (Exception e) {
            uploadDir = SpringUtil.getBean(Environment.class).getProperty("file.properties.local.uploadDir", String.class, "tmp/uploads");
        }
        if (!uploadDir.endsWith(separator)) {
            uploadDir = uploadDir + separator;
        }
        uploadDir = (uploadDir + getBucket() + separator).replace(separator + separator, separator).replace(separator + separator, separator);
        File file = FileUtils.newFile(uploadDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        String absolutePath = FileUtils.getAbsolutePath(file);
        if (!absolutePath.endsWith(separator)) {
            absolutePath = absolutePath + separator;
        }
        return absolutePath;
    }


    public static String getBucket() {
        String directory;
        try {
            FileProperties fileProperties = SpringUtil.getBean(FileProperties.class);
            FileProperties.LocalProperties local = fileProperties.getLocal();
            directory = local.getDirectory();
        } catch (Exception e) {
            directory = SpringUtil.getBean(Environment.class).getProperty("file.properties.local.directory");
        }
        return ObjectUtils.defaultIfBlank(directory, "local");
    }

    public static String getChunkDir() {
        String separator = OSConfig.separator;
        String chunk = getUploadDir() + "chunks" + separator;
        File file = FileUtils.newFile(chunk);
        if (!file.exists()) {
            file.mkdirs();
        }
        return chunk;
    }

    public static String getMergeDir() {
        String separator = OSConfig.separator;
        String merged = getUploadDir() + "merged" + separator;
        File file = FileUtils.newFile(merged);
        if (!file.exists()) {
            file.mkdirs();
        }
        return merged;
    }

    public static int getChunkSize() {
        // 每个分片大小为1MB
        return 1024 * 1024;
    }

    public static String getPartSuffix() {
        return Constants.PART_SUFFIX;
    }

    public static String getMergeFilePath(String identifier, String fileMainName) {
        String separator = OSConfig.separator;
        String path = getMergeDir() + identifier + separator;
        path = path.replace(separator + separator, separator).replace(separator + separator, separator);
        File file = FileUtils.newFile(path);
        if (!file.exists()) {
            file.mkdirs();
        }
        return path + fileMainName;
    }

    public static String getChunkDirPath(String identifier) {
        String separator = OSConfig.separator;
        String chunkDir = getChunkDir() + identifier + separator;
        return chunkDir.replace(separator + separator, separator).replace(separator + separator, separator);
    }

    /*===========================================================================================================================================================================================================================================*/

    /**
     * 上传文件
     *
     * @param flieName
     * @param inputStream
     * @return
     */
    public static String upload(String flieName, InputStream inputStream) {
        return upload(null, flieName, inputStream);
    }

    /**
     * 上传文件
     *
     * @param bucketName
     * @param flieName
     * @param inputStream
     */
    @SneakyThrows
    public static String upload(String bucketName, String flieName, InputStream inputStream) {
        if (StrUtil.isBlank(bucketName)) {
            bucketName = "";
        }
        String separator = OSConfig.separator;
        String bucketPath = getUploadDir() + separator + bucketName + separator;
        bucketPath = bucketPath.replace(separator + separator, separator).replace(separator + separator, separator);

        File bucketFile = FileUtils.newFile(bucketPath);
        if (!bucketFile.exists()) {
            bucketFile.mkdirs();
        }
        putRedisFile(bucketPath);
        File file = FileUtils.newFile(bucketPath + flieName);
        if (!file.exists()) {
            file.createNewFile();
        }
        IoUtils.copy(inputStream, FileUtils.getOutputStream(file));
        putRedisFile(file);
        return FileUtils.getAbsolutePath(file);
    }

    /**
     * 分片上传
     *
     * @return
     */
    @SneakyThrows
    public static String uploadSharding(String fileName, String identifier, InputStream input) {
        splitFileLocal(identifier, input);
        String fileLocal = mergeFileLocal(fileName, identifier);
        putRedisFile(fileLocal);
        return FileUtils.getAbsolutePath(fileLocal);
    }

    /**
     * 将指定文件拆分成若干个分块文件
     *
     * @param identifier
     * @param input
     */
    public static void splitFileLocal(String identifier, InputStream input) {
        splitFileLocal(getChunkDir(), identifier, input);
    }

    /**
     * 将指定文件拆分成若干个分块文件
     *
     * @param chunkDir   分块文件存放目录
     * @param identifier 唯一值
     * @param input      原始文件输入流
     */
    @SneakyThrows
    public static void splitFileLocal(String chunkDir, String identifier, InputStream input) {
        if (StrUtil.isBlank(chunkDir)) {
            chunkDir = getChunkDir();
        }
        String separator = OSConfig.separator;
        if (!chunkDir.endsWith(separator)) {
            chunkDir = chunkDir + separator;
        }
        int chunkNumber = 1;
        // 创建分块存放目录：CHUNK_DIR + identifier
        String chunkDirPath = chunkDir + identifier;
        File chunkDirFile = FileUtils.newFile(chunkDirPath);
        if (!chunkDirFile.exists()) {
            chunkDirFile.mkdirs();
        }
        putRedisFile(chunkDirFile);
        try (InputStream fis = input) {
            byte[] buffer = new byte[getChunkSize()];
            int bytesRead;
            // 循环读取，直到返回 -1 表示读完
            while ((bytesRead = fis.read(buffer)) != -1) {
                // 每个分块文件命名为 “chunkNumber.part”
                File tempFile = FileUtils.newFile(chunkDirPath + separator + chunkNumber + getPartSuffix());
                if (!tempFile.exists()) {
                    tempFile.createNewFile();
                }
                putRedisFile(tempFile);
                // 写入读取的字节到分块文件中
                try (OutputStream os = new FileOutputStream(tempFile)) {
                    os.write(buffer, 0, bytesRead);
                }
                chunkNumber++;
            }
        }
    }

    /**
     * 上传单个分片
     *
     * @param chunkNumber
     * @param identifier
     * @param input
     */
    public static String splitChunkNumberFileLocal(int chunkNumber, String identifier, InputStream input) {
        return splitChunkNumberFileLocal(getChunkDir(), chunkNumber, identifier, input);
    }

    /**
     * 上传单个分片
     *
     * @param chunkDir
     * @param chunkNumber
     * @param identifier
     * @param input
     */
    @SneakyThrows
    public static String splitChunkNumberFileLocal(String chunkDir, int chunkNumber, String identifier, InputStream input) {
        // 创建分块存放目录：CHUNK_DIR + identifier
        String chunkDirPath = chunkDir + identifier;
        File chunkDirFile = FileUtils.newFile(chunkDirPath);
        if (!chunkDirFile.exists()) {
            chunkDirFile.mkdirs();
        }
        String separator = OSConfig.separator;
        File tempFile = FileUtils.newFile(chunkDirPath + separator + chunkNumber + getPartSuffix());
        if (!tempFile.exists()) {
            tempFile.createNewFile();
        }
        BufferedOutputStream outputStream = FileUtils.getOutputStream(tempFile);
        IoUtils.copy(input, outputStream);
        putRedisFile(tempFile);
        return FileUtils.getAbsolutePath(tempFile);
    }

    /**
     * 将分块文件合并为一个完整文件，并清理临时分块文件
     *
     * @param fileName
     * @param identifier
     * @return url
     */
    public static String mergeFileLocal(String fileName, String identifier) {
        return mergeFileLocal(getChunkDir(), getMergeDir(), fileName, identifier);
    }

    /**
     * 将分块文件合并为一个完整文件，并清理临时分块文件
     *
     * @param chunkDir   分片文件夹(不包含唯一值)
     * @param mergeDir   合并文件夹
     * @param fileName   原始文件名
     * @param identifier 唯一值
     * @return url
     */
    @SneakyThrows
    public static String mergeFileLocal(String chunkDir, String mergeDir, String fileName, String identifier) {
        String separator = OSConfig.separator;
        if (!chunkDir.endsWith(separator)) {
            chunkDir = chunkDir + separator;
        }
        if (!mergeDir.endsWith(separator)) {
            mergeDir = mergeDir + separator;
        }
        mergeDir = mergeDir + identifier + separator;
        // 分块文件所在目录
        File chunkDirFile = FileUtils.newFile(chunkDir + identifier);
        File[] chunkFiles = chunkDirFile.listFiles();
        if (chunkFiles == null || chunkFiles.length == 0) {
            throw new RuntimeException("No chunk files found for merging");
        }

        // 按分块编号排序（假设分块文件名为 "1.part", "2.part", …）
        List<File> sortedChunks = Arrays.stream(chunkFiles)
                .sorted(Comparator.comparingInt(f -> Integer.parseInt(FileUtils.mainName(f))))
                .collect(Collectors.toList());

        // 创建合并后文件存放目录
        File mergeDirFile = FileUtils.newFile(mergeDir);
        if (!mergeDirFile.exists()) {
            mergeDirFile.mkdirs();
        }
        // 合并后完整文件路径：MERGE_DIR + 原始文件名
        String mergeFile = mergeDir + fileName;
        File mergedFile = FileUtils.newFile(mergeFile);
        // 如果目标文件已存在，则先删除
        if (mergedFile.exists()) {
            mergedFile.delete();
        }
        mergedFile.createNewFile();
        // 依次将每个分块文件的内容复制到合并后的文件中
        try (OutputStream outputStream = new FileOutputStream(mergedFile)) {
            for (File chunk : sortedChunks) {
                try (InputStream inputStream = new FileInputStream(chunk)) {
                    IoUtil.copy(inputStream, outputStream);
                }
            }
        }
        sortedChunks.add(chunkDirFile);
        sortedChunks.stream().forEach(file -> {
            String path = FileUtils.getAbsolutePath(file);
            FileUtils.del(path);
            delRedisFile(path);
        });
        putRedisFile(mergedFile);
        return FileUtils.getAbsolutePath(mergedFile);
    }

    /**
     * 获取分块文件列表
     *
     * @param identifier
     * @param fileId
     * @return
     */
    public static List<FilePart> getFileParts(String identifier, Long fileId) {
        return getFileParts(getChunkDir(), identifier, fileId);
    }

    /**
     * 获取分块文件列表
     *
     * @param chunkDir
     * @param identifier
     * @param fileId
     * @return
     */
    public static List<FilePart> getFileParts(String chunkDir, String identifier, Long fileId) {
        String separator = OSConfig.separator;
        if (!chunkDir.endsWith(separator)) {
            chunkDir = chunkDir + separator;
        }
        // 分块文件所在目录
        File chunkDirFile = FileUtils.newFile(chunkDir + identifier);
        File[] chunkFiles = chunkDirFile.listFiles();
        if (chunkFiles == null || chunkFiles.length == 0) {
            throw new RuntimeException("No chunk files found ");
        }
        // 按分块编号排序（假设分块文件名为 "1.part", "2.part", …）
        List<FilePart> fileParts = Arrays.stream(chunkFiles).filter(file -> ("." + FileUtils.getSuffix(file)).endsWith(getPartSuffix())).map(file ->
                new FilePart()
                        .setFileId(fileId)
                        .setLocal(Boolean.TRUE)
                        .setLocalResource(FileUtils.getAbsolutePath(file))
                        .setPartCode(identifier)
                        .setPartSort(Integer.parseInt(FileUtils.mainName(file)))
                        .setPartSize(IoUtils.size(FileUtils.getInputStream(file)))
        ).sorted(Comparator.comparingInt(FilePart::getPartSort)).collect(Collectors.toList());
        return fileParts;
    }

    /**
     * 获取分块文件路径
     *
     * @param identifier
     * @param totalChunks
     * @return
     */
    public static List<InputStream> getSplitFileLocal(String identifier, int totalChunks) {
        List<InputStream> streamList = CollUtil.newArrayList();
        List<FilePart> fileParts = getFileParts(identifier, null);
        streamList.addAll(fileParts.stream().filter(FilePart::getLocal).map(filePart -> FileUtils.getInputStream(filePart.getLocalResource())).collect(Collectors.toList()));
        if (!ObjectUtils.equals(totalChunks, streamList.size())) {
            throw new GlobalCustomException("文件分块数量不匹配");
        }
        return streamList;
    }

    /**
     * 判断是否是文件夹
     *
     * @param separator
     * @param path
     * @return
     */
    private static String isDir(String separator, String path) {
        if (FileUtils.isDirectory(path)) {
            if (!path.endsWith(separator)) {
                path = path + separator;
            }
        }
        return path;
    }

    /**
     * 获取 UploadDir文件夹下文件路径  mergeDir,chunkDir 文件夹下文件夹路径
     *
     * @return
     */
    public static List<String> getFileNameList() {
        String separator = OSConfig.separator;
        String uploadDir = getUploadDir();
        String mergeDir = getMergeDir();
        String chunkDir = getChunkDir();

        List<String> fileNameList = Arrays.stream(FileUtils.newFile(uploadDir).listFiles())
                .map(FileUtils::getAbsolutePath)
                .map(path -> isDir(separator, path))
                .filter(ObjectUtils::isNotEmpty)
                .collect(Collectors.toList());

        List<String> mergeDirList = Arrays.stream(FileUtils.newFile(mergeDir).listFiles())
                .map(FileUtils::getAbsolutePath)
                .map(path -> isDir(separator, path))
                .filter(ObjectUtils::isNotEmpty)
                .collect(Collectors.toList());

        List<String> chunkDirList = Arrays.stream(FileUtils.newFile(chunkDir).listFiles())
                .map(FileUtils::getAbsolutePath)
                .map(path -> isDir(separator, path))
                .filter(ObjectUtils::isNotEmpty)
                .collect(Collectors.toList());

        List<String> fileList = CollUtil.newArrayList();
        mergeDirList.stream().filter(FileUtils::isDirectory).forEach(path ->
                fileList.addAll(
                        Arrays.stream(FileUtils.newFile(path).listFiles())
                                .filter(FileUtils::isFile)
                                .map(FileUtils::getAbsolutePath)
                                .filter(ObjectUtils::isNotEmpty)
                                .collect(Collectors.toList()))
        );

        chunkDirList.stream().filter(FileUtils::isDirectory).forEach(path ->
                fileList.addAll(
                        Arrays.stream(FileUtils.newFile(path).listFiles())
                                .filter(FileUtils::isFile)
                                .map(FileUtils::getAbsolutePath)
                                .filter(ObjectUtils::isNotEmpty)
                                .collect(Collectors.toList()))
        );

        Set<String> list = CollUtil.newHashSet(fileNameList);
        list.addAll(mergeDirList);
        list.addAll(chunkDirList);
        list.addAll(fileList);
        list.removeAll(CollUtil.newArrayList(uploadDir, mergeDir, chunkDir));
        return CollUtil.newArrayList(list);
    }


}
