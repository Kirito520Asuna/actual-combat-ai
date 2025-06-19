package com.actual.combat.basic.core.utils.io;

import cn.hutool.core.io.IoUtil;
import com.actual.combat.basic.core.utils.file.FileUtils;
import com.actual.combat.basic.exceptions.GlobalCustomException;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * @Author yan
 * @Date 2025/3/7 21:07:13
 * @Description
 */
@Slf4j
public class IoUtils extends IoUtil {
    @SneakyThrows
    public static long size(InputStream inputStream) {
        long size = 0;
        if (inputStream != null) {
            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                size += bytesRead;
            }
        }
        return size;
    }

    @SneakyThrows
    public static byte[] toByteArray(InputStream inputStream) {
        return IOUtils.toByteArray(inputStream);
    }

    @SneakyThrows
    public static byte[] toByteArray(InputStream input, int size) {
        return IOUtils.toByteArray(input, size);
    }

    @SneakyThrows
    public static byte[] toByteArray(InputStream input, long size) {
        return IOUtils.toByteArray(input, size);
    }

    @SneakyThrows
    public static byte[] toByteArray(Reader reader) {
        return IOUtils.toByteArray(reader);
    }

    @SneakyThrows
    public static byte[] toByteArray(Reader reader, Charset charset) {
        return IOUtils.toByteArray(reader, charset);
    }

    @SneakyThrows
    public static byte[] toByteArray(Reader reader, String charsetName) {
        return IOUtils.toByteArray(reader, charsetName);
    }

    @SneakyThrows
    public static byte[] toByteArray(String input) {
        return IOUtils.toByteArray(input);
    }

    @SneakyThrows
    public static byte[] toByteArray(URI uri) throws IOException {
        return IOUtils.toByteArray(uri);
    }

    @SneakyThrows
    public static byte[] toByteArray(URL url) {
        return IOUtils.toByteArray(url);

    }

    @SneakyThrows
    public static byte[] toByteArray(URLConnection urlConn) {
        return IOUtils.toByteArray(urlConn);
    }

    @SneakyThrows
    public static byte[] toByteArray(List<InputStream> inList) {
        return merge(inList).toByteArray();
    }

    /**
     * 将输入流按指定大小分割成多个输入流
     *
     * @param in
     * @param chunkSize
     * @return
     */
    @SneakyThrows
    public static List<InputStream> splitInputStream(InputStream in, int chunkSize) {
        List<InputStream> result = new ArrayList<>();
        byte[] buffer = new byte[chunkSize];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            // 如果最后一次读取的字节数不足 chunkSize，复制有效数据
            byte[] chunkData = (bytesRead == chunkSize) ? buffer.clone() : Arrays.copyOf(buffer, bytesRead);
            result.add(new ByteArrayInputStream(chunkData));
        }
        return result;
    }

    /**
     * 合并多个输入流
     *
     * @param inputStreams
     * @return
     */
    @SneakyThrows
    public static ByteArrayInputStream mergeInputStream(List<InputStream> inputStreams) {
        return new ByteArrayInputStream(toByteArray(inputStreams));
    }

    /**
     * 内存不足会出现 java heap space 异常
     *
     * @param list
     * @return
     */
    @SneakyThrows
    public static ByteArrayOutputStream merge(List<InputStream> list) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            for (InputStream chunk : list) {
                try (InputStream inputStream = chunk) {
                    IOUtils.copy(inputStream, out); // 使用 Apache Commons IO 高效拷贝
                }
            }
        } catch (IOException e) {
            throw new GlobalCustomException("合并错误！" + e);
        }
        return out;
    }

    public static byte[] mergeStreams(List<InputStream> inputStreams,
                                    int bufferSize) throws IOException {
        // 创建临时文件（自动删除保障）
        File tempFile = File.createTempFile("merged-", null);

        try (FileOutputStream fos = new FileOutputStream(tempFile);
             BufferedOutputStream bos = new BufferedOutputStream(fos, bufferSize)) {

            byte[] buffer = new byte[bufferSize];
            for (InputStream is : inputStreams) {
                try (BufferedInputStream bis = new BufferedInputStream(is, bufferSize)) {
                    int bytesRead;
                    while ((bytesRead = bis.read(buffer)) != -1) {
                        bos.write(buffer, 0, bytesRead);
                        bos.flush(); // 确保及时刷入磁盘
                    }
                } catch (IOException e) {
                    throw new IOException("合并分片时发生IO异常", e);
                } finally {
                    // 强制释放资源（防御性编程）
                    close(is);
                }
            }
            return toByteArray(FileUtils.getInputStream(tempFile));
        } catch (IOException e) {
            // 合并失败时删除临时文件
            if (tempFile.exists() && !tempFile.delete()) {
                log.error("无法删除临时文件: " + tempFile.getAbsolutePath());
            }
            throw e;
        }finally {
            FileUtils.del(tempFile);
        }
    }

    @SneakyThrows
    public static long getInputStreamLength(byte[] bytes) {
        return getInputStreamLength(new ByteArrayInputStream(bytes));
    }

    @SneakyThrows
    public static long getInputStreamLength(InputStream inputStream) {
        byte[] buffer = new byte[1024];
        long length = 0;
        int bytesRead;
        // 防止流耗尽
        byte[] bytes = toByteArray(inputStream);
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        while ((bytesRead = byteArrayInputStream.read(buffer)) != -1) {
            length += bytesRead;
        }
        return length;
    }


    /**
     * 合并多个文件分片并返回一个字节数组。
     *
     * @param chunks 文件的分片列表，每个元素是一个字节数组。
     * @param suffix 临时文件的后缀，例如 ".txt" 或 ".bin"
     * @return 合并后的字节数组。
     * @throws IOException 如果读取或写入文件发生错误。
     */
    public static byte[] mergeChunksAndReturnBytes(List<byte[]> chunks, String suffix) throws IOException {
        // 确保临时文件存放目录存在
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "merged_files");
        if (!tempDir.exists() && !tempDir.mkdirs()) {
            throw new IOException("无法创建临时目录: " + tempDir.getAbsolutePath());
        }
        // 创建唯一的临时文件
        File tempFile = File.createTempFile("tmp_merged_" + UUID.randomUUID() + System.currentTimeMillis(), suffix, tempDir);

        // 通过临时文件路径创建输出流，准备将各个分片写入文件
        try (BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(tempFile))) {
            // 遍历每个分片并写入临时文件
            for (byte[] chunk : chunks) {
                os.write(chunk);  // 逐个分片写入文件
            }
            os.flush();  // 确保所有数据写入完成
        } catch (IOException ex) {
            // 出现异常时删除临时文件
            log.error("合并文件过程中出现异常：{}", ex.getMessage());
            FileUtils.del(tempFile);  // 删除临时文件以避免文件残留
            throw ex;  // 抛出原始异常
        }

        // 采用逐块读取的方式来避免一次性加载到内存中，减少内存压力
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             InputStream inputStream = new FileInputStream(tempFile)) {
            byte[] buffer = new byte[8192];  // 8KB 缓冲区，适当调整以平衡性能与内存消耗
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);  // 写入缓冲区中的数据
            }

            byte[] fileBytes = byteArrayOutputStream.toByteArray();  // 获取合并后的字节数组

            // 删除临时文件
            FileUtils.del(tempFile);

            return fileBytes;  // 返回合并后的字节数组
        } catch (IOException ex) {
            // 读取文件时出现异常，删除临时文件
            log.error("读取临时文件失败：{}", ex.getMessage());
            FileUtils.del(tempFile);  // 删除临时文件以避免文件残留
            throw ex;  // 抛出原始异常
        }
    }

    /**
     * 合并多个输入流并返回合并后的字节数组
     *
     * @param chunks 输入流列表，每个流表示一个文件分片
     * @param suffix 临时文件的后缀，例如 ".txt" 或 ".bin"
     * @return 合并后的字节数组
     * @throws IOException 读取或写入文件时的异常
     */
    public static byte[] mergeStreamsAndReturnBytes(List<InputStream> chunks, String suffix) throws IOException {
        // 确保临时文件存放目录存在
        File tempDir = new File(System.getProperty("java.io.tmpdir"), "merged_files");
        if (!tempDir.exists() && !tempDir.mkdirs()) {
            throw new IOException("无法创建临时目录: " + tempDir.getAbsolutePath());
        }

        // 创建唯一的临时文件
        File tempFile = File.createTempFile("tmp_merged_" + UUID.randomUUID() + System.currentTimeMillis(), suffix, tempDir);

        // 将所有输入流写入临时文件
        try (BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(tempFile))) {
            byte[] buffer = new byte[8192]; // 8KB 缓冲区
            for (InputStream chunk : chunks) {
                try (InputStream is = chunk) {  // 确保每个输入流都能被正确关闭
                    int bytesRead;
                    while ((bytesRead = is.read(buffer)) != -1) {
                        os.write(buffer, 0, bytesRead);
                    }
                }
            }
            os.flush();
        } catch (IOException ex) {
            log.error("合并流时发生错误: {}", ex.getMessage());
            FileUtils.del(tempFile);
            throw ex;
        }

        // 逐块读取合并后的文件内容到内存
        try (ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
             InputStream inputStream = new FileInputStream(tempFile)) {
            byte[] buffer = new byte[8192];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, bytesRead);
            }

            byte[] fileBytes = byteArrayOutputStream.toByteArray();

            // 删除临时文件
            FileUtils.del(tempFile);

            return fileBytes;
        } catch (IOException ex) {
            log.error("读取临时文件失败: {}", ex.getMessage());
            FileUtils.del(tempFile);
            throw ex;
        }
    }
}
