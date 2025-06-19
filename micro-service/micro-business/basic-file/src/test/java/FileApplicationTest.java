import cn.hutool.core.io.FileUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpUtil;
import com.actual.combat.BasicFileApplication;
import com.actual.combat.basic.core.utils.io.IoUtils;
import com.actual.combat.basic.file.core.utils.oss.LocalOSSUtils;
import com.actual.combat.file.controller.FileController;
import jakarta.annotation.Resource;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.io.*;
import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * @Author yan
 * @Date 2025/3/8 12:52:49
 * @Description
 */
@AutoConfigureMockMvc
//@SpringBootTest(classes = FileApplication.class)
@WebMvcTest(controllers = BasicFileApplication.class)
public class FileApplicationTest {
    @Resource
    private MockMvc mockMvc;
    @MockBean
    private FileController fileController; // 模拟控制器中依赖的 fileService
    @Test
    void test() {
        String fileName = "【不忘初心】Windows10_21H2_19044.1586_X64_可更新[纯净精简版][2.53G](2022.3(1).9).zip";
        String identifier = UUID.randomUUID().toString() + System.currentTimeMillis();
        String path = "G:\\zip-all\\【不忘初心】Windows10_21H2_19044.1586_X64_可更新[纯净精简版][2.53G](2022.3(1).9).zip";
        InputStream inputStream = FileUtil.getInputStream(path);
        LocalOSSUtils.uploadSharding(fileName, identifier, inputStream);
        //LocalOSSUtils.splitFileLocal(fileName, identifier, inputStream);
        //LocalOSSUtils.mergeFileLocal(fileName, identifier);
    }

    @SneakyThrows
    @Test
    void splitInputStream() {
        // 示例：将字符串转为 InputStream，然后拆分，每块 10 字节
        String data = "这是一段用于测试的长文本数据，用来演示 InputStream 的拆分。";
        InputStream in = new ByteArrayInputStream(data.getBytes("UTF-8"));

        List<InputStream> parts = IoUtils.splitInputStream(in, 1024 * 1024);
        System.out.println("拆分后的流数量: " + parts.size());
        //遍历每个拆分的 InputStream 并打印内容
        int partNum = 1;
        for (InputStream part : parts) {
            byte[] partBytes = new byte[part.available()];
            part.read(partBytes);
            String partStr = new String(partBytes, "UTF-8");
            System.out.println("Part " + partNum++ + ": " + partStr);
        }
    }

    @SneakyThrows
    @Test
    void mergeInputStream() {
        // 示例：将字符串转为 InputStream，然后拆分，每块 10 字节
        String data = "这是一段用于测试的长文本数据，用来演示 InputStream 的拆分。";
        InputStream in = new ByteArrayInputStream(data.getBytes("UTF-8"));

        List<InputStream> parts = IoUtils.splitInputStream(in, 10);
        System.out.println("拆分后的流数量: " + parts.size());
        InputStream inMerge = IoUtils.mergeInputStream(parts);

        String MergeStr = new String(IoUtils.toByteArray(inMerge), "UTF-8");
        System.out.println("Merge :" + MergeStr);
    }

    @SneakyThrows
    @Test
    void uploadChunk() {
        String fileName = "【不忘初心】Windows10_21H2_19044.1586_X64_可更新[纯净精简版][2.53G](2022.3(1).9).zip";
        String identifier = UUID.randomUUID().toString() + System.currentTimeMillis();
        String path = "G:\\zip-all\\【不忘初心】Windows10_21H2_19044.1586_X64_可更新[纯净精简版][2.53G](2022.3(1).9).zip";

        InputStream inputStream = FileUtil.getInputStream(path);

        List<InputStream> streamList = IoUtils.splitInputStream(inputStream, 1024 * 1024);

        String uploadUrl = "http://127.0.0.1:13600/file/file/upload/chunk";
        int chunkNumber = 1;
        int totalChunks = streamList.size();

        for (InputStream input : streamList) {
            uploadChunk(uploadUrl, identifier, chunkNumber, totalChunks, input);
            chunkNumber++;
        }

        String mergeUrl = "http://127.0.0.1:13600/file/file/upload/merge";
        merge(mergeUrl, null, identifier);
    }

    private void uploadChunk(String url, String identifier, int chunkNumber, int totalChunks, InputStream inputStream) throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", inputStream);

        // 执行模拟请求并验证结果
        mockMvc.perform(multipart(url)
                        .file(file)
                        .param("chunkNumber", String.valueOf(chunkNumber))
                        .param("totalChunks", String.valueOf(totalChunks))
                        .param("identifier", identifier))
                .andExpect(status().isOk())
                .andExpect(content().string("Chunk uploaded successfully"));
    }

    private void merge(String url, Long fileId, String identifier) {
        System.err.println("Merging chunks...");
        Map<String, Object> params = new HashMap<>();
        params.put("fileId", fileId);
        params.put("identifier", identifier);

        HttpRequest request = HttpUtil.createPost(url);
        request.form(params);

        String response = request.execute().body();
        System.err.println("Chunk " + identifier + " Merging. Server response: " + response);
    }

}
