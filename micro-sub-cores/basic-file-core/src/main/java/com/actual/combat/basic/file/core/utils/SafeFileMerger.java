package com.actual.combat.basic.file.core.utils;



import com.actual.combat.basic.core.utils.file.FileUtils;

import java.io.*;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.*;
import java.util.List;
import java.util.UUID;

/**
 * @Author yan
 * @Date 2025/3/22 21:40:01
 * @Description 安全合并文件分片工具类，支持合并到文件或内存，并避免OOM
 */
public class SafeFileMerger {
    // 默认配置参数
    private static final int DEFAULT_BUFFER_SIZE = 8192; // 8KB
    private static final long MAX_MEMORY_SIZE = 200 * 1024 * 1024; // 200MB
    private static final String DEFAULT_TEMP_DIR = System.getProperty("java.io.tmpdir") + File.separator + "merged_files";
    private static final int TEMP_FILE_RETRY = 3; // 临时文件创建重试次数

    /**
     * 合并输入流到临时文件（大文件安全方案），返回临时文件的绝对路径
     *
     * @param inputStreams 输入流列表
     * @return 合并后的临时文件绝对路径
     * @throws IOException 如果合并过程中出错
     */
    public static String mergeToFile(List<InputStream> inputStreams) throws IOException {
        return mergeToFile(inputStreams, DEFAULT_BUFFER_SIZE, DEFAULT_TEMP_DIR);
    }

    /**
     * 合并输入流到临时文件（带自定义配置），返回临时文件的绝对路径
     *
     * @param inputStreams 输入流列表
     * @param bufferSize   缓冲区大小（建议8KB-64KB）
     * @param tempDir      临时目录路径
     * @return 合并后临时文件的绝对路径
     * @throws IOException 如果合并过程中出错
     */
    public static String mergeToFile(List<InputStream> inputStreams,
                                     int bufferSize,
                                     String tempDir) throws IOException {
        FileUtils.newFile(tempDir).mkdirs();
        // 防御性校验
        validateInput(inputStreams, bufferSize, tempDir);

        Path tempPath = createTempFile(tempDir);
        try {
            // 使用 NIO 通道提升性能
            try (FileChannel destChannel = FileChannel.open(tempPath, StandardOpenOption.WRITE, StandardOpenOption.CREATE)) {
                for (InputStream is : inputStreams) {
                    try (ReadableByteChannel srcChannel = Channels.newChannel(is)) {
                        long transferred = destChannel.transferFrom(srcChannel, destChannel.size(), Long.MAX_VALUE);
                        if (transferred == 0) {
                            throw new IOException("零字节传输，可能输入流已关闭");
                        }
                    }
                }
            }
            return tempPath.toAbsolutePath().toString();
        } catch (IOException e) {
            deleteQuietly(tempPath);
            throw e;
        }
    }

    /**
     * 合并输入流到指定的文件（大文件安全方案）
     * @param inputStreams
     * @param outputFilePath
     * @return
     * @throws IOException
     */
    public static String mergeToFile(List<InputStream> inputStreams, String outputFilePath) throws IOException {
        return mergeToFile(inputStreams, outputFilePath, DEFAULT_BUFFER_SIZE);
    }
    /**
     * 重载方法：合并输入流到指定的文件
     * 如果指定文件不存在，则递归创建父目录和文件。
     *
     * @param inputStreams   输入流列表
     * @param outputFilePath 指定的文件全路径名
     * @param bufferSize     缓冲区大小（建议8KB-64KB）
     * @return 指定文件的绝对路径
     * @throws IOException 如果合并过程中出错
     */
    public static String mergeToFile(List<InputStream> inputStreams, String outputFilePath, int bufferSize) throws IOException {
        // 防御性校验
        if (inputStreams == null || inputStreams.isEmpty()) {
            throw new IllegalArgumentException("输入流列表不能为空");
        }
        if (bufferSize <= 0 || bufferSize > 1024 * 1024) {
            throw new IllegalArgumentException("缓冲区大小需在1B-1MB之间");
        }
        if (outputFilePath == null || outputFilePath.trim().isEmpty()) {
            throw new IllegalArgumentException("输出文件路径不能为空");
        }

        File outputFile = new File(outputFilePath);
        // 如果输出文件的父目录不存在，递归创建
        File parentDir = outputFile.getParentFile();
        if (!parentDir.exists() && !parentDir.mkdirs()) {
            throw new IOException("无法创建输出文件的父目录: " + parentDir.getAbsolutePath());
        }

        if (!outputFile.exists()&&!outputFile.createNewFile()){
            throw new IOException("无法创建输出文件: " + outputFile.getAbsolutePath());
        }
        // 打开输出文件的通道
        try (FileChannel destChannel = FileChannel.open(outputFile.toPath(),
                StandardOpenOption.CREATE, StandardOpenOption.WRITE, StandardOpenOption.TRUNCATE_EXISTING)) {
            for (InputStream is : inputStreams) {
                try (ReadableByteChannel srcChannel = Channels.newChannel(is)) {
                    long transferred = destChannel.transferFrom(srcChannel, destChannel.size(), Long.MAX_VALUE);
                    if (transferred == 0) {
                        throw new IOException("零字节传输，可能输入流已关闭");
                    }
                }
            }
        }
        return outputFile.getAbsolutePath();
    }

    /**
     * 合并输入流到内存（小文件专用方案）
     *
     * @return 合并后的字节数组
     * @throws FileTooLargeException 如果合并后文件大小超过限制
     * @throws IOException           如果合并过程中出错
     */
    public static byte[] mergeToMemory(List<InputStream> inputStreams) throws IOException {
        return mergeToMemory(inputStreams, DEFAULT_BUFFER_SIZE, MAX_MEMORY_SIZE);
    }

    /**
     * 合并输入流到内存（带自定义限制）
     *
     * @param inputStreams  输入流列表
     * @param bufferSize    缓冲区大小
     * @param maxMemorySize 最大允许内存占用
     * @return 合并后的字节数组
     * @throws FileTooLargeException 如果合并后文件大小超过限制
     * @throws IOException           如果读取出错
     */
    public static byte[] mergeToMemory(List<InputStream> inputStreams,
                                       int bufferSize,
                                       long maxMemorySize) throws IOException {
        FileUtils.newFile(DEFAULT_TEMP_DIR).mkdirs();
        // 防御性校验
        validateInput(inputStreams, bufferSize, DEFAULT_TEMP_DIR);

        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            long totalBytes = 0;
            byte[] buffer = new byte[bufferSize];

            for (InputStream is : inputStreams) {
                try (BufferedInputStream bis = new BufferedInputStream(is, bufferSize)) {
                    int bytesRead;
                    while ((bytesRead = bis.read(buffer)) != -1) {
                        baos.write(buffer, 0, bytesRead);
                        totalBytes += bytesRead;
                        // 内存占用检查
                        if (totalBytes > maxMemorySize) {
                            throw new FileTooLargeException("合并后文件大小 " + totalBytes + " 超过限制 " + maxMemorySize);
                        }
                    }
                }
            }
            return baos.toByteArray();
        }
    }

    // region 核心工具方法

    /**
     * 创建临时文件，如果失败则重试指定次数
     *
     * @param tempDir 临时文件目录
     * @return 临时文件路径
     * @throws IOException 如果创建失败
     */
    private static Path createTempFile(String tempDir) throws IOException {
        Path dir = Paths.get(tempDir);
        checkDirWritable(dir); // 目录可写检查

        int retryCount = 0;
        IOException lastException = null;
        while (retryCount < TEMP_FILE_RETRY) {
            try {
                return Files.createTempFile(dir, "merged_" + UUID.randomUUID() + "_" + System.currentTimeMillis(), ".tmp");
            } catch (IOException e) {
                lastException = e;
                retryCount++;
            }
        }
        throw new IOException("创建临时文件失败（重试" + TEMP_FILE_RETRY + "次）", lastException);
    }

    /**
     * 校验输入参数有效性
     */
    private static void validateInput(List<InputStream> inputStreams, int bufferSize, String tempDir) throws IOException {
        if (inputStreams == null || inputStreams.isEmpty()) {
            throw new IllegalArgumentException("输入流列表不能为空");
        }
        if (bufferSize <= 0 || bufferSize > 1024 * 1024) {
            throw new IllegalArgumentException("缓冲区大小需在1B-1MB之间");
        }
        checkDirWritable(Paths.get(tempDir));
    }

    /**
     * 检查指定目录是否存在、为文件夹且可写
     */
    private static void checkDirWritable(Path dir) throws IOException {
        if (!Files.exists(dir)) {
            throw new IOException("临时目录不存在: " + dir);
        }
        if (!Files.isDirectory(dir)) {
            throw new IOException("临时目录路径不是文件夹: " + dir);
        }
        if (!Files.isWritable(dir)) {
            throw new IOException("临时目录不可写: " + dir);
        }
    }

    /**
     * 静默删除文件，若删除失败则忽略异常
     */
    private static void deleteQuietly(Path path) {
        try {
            Files.deleteIfExists(path);
        } catch (IOException ignored) {
        }
    }

    // endregion

    // region 自定义异常
    public static class FileTooLargeException extends IOException {
        public FileTooLargeException(String message) {
            super(message);
        }
    }
    // endregion

    // region 使用示例（注释，仅供参考）
    /*
    public static void main(String[] args) {
        // 示例1：合并到文件（大文件推荐）
        try {
            String mergedPath = SafeFileMerger.mergeToFile(inputStreams, "C:/output/mergedFile.zip", DEFAULT_BUFFER_SIZE);
            System.out.println("合并文件路径: " + mergedPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 示例2：合并到内存（小文件专用）
        try {
            byte[] data = SafeFileMerger.mergeToMemory(inputStreams);
            System.out.println("合并数据大小: " + data.length + " bytes");
        } catch (SafeFileMerger.FileTooLargeException e) {
            System.err.println("文件过大，请使用mergeToFile方法");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    */
    // endregion
}
