import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.actual.combat.basic.core.utils.io.IoUtils;
import com.actual.combat.basic.result.Result;
import com.actual.combat.basic.utils.http.OkHttpUtils;
import com.actual.combat.user.dto.FileUpDto;
import com.googlecode.aviator.AviatorEvaluator;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;


/**
 * @Author yan
 * @Date 2025/3/8 18:11:03
 * @Description
 */
@Slf4j
public class UploadTest {
    public static void main(String[] args) {
        boolean useExecutor = true;
        uploadChunk(useExecutor);
        //System.err.println(FileUtil.mainName("G:\\zip-all\\tbtool.7z") + "." + FileUtil.getSuffix("G:\\zip-all\\tbtool.7z"));
    }

    private static final int THREAD_COUNT = 4; // 线程池大小，可调整

    @SneakyThrows
    public static void uploadChunk(boolean useExecutor) {
        String host = "192.168.3.224:13600";

        //String identifier = UUID.randomUUID().toString() + System.currentTimeMillis();
        String path = "/home/yan/下载/JsScript.zip";
        String fileName = FileUtil.getName(path);

        FileUpDto fileUpDto = new FileUpDto();
        fileUpDto.setFileName(fileName)
                .setDir(Boolean.FALSE).setImg(Boolean.FALSE)
                .setType(FileUtil.getSuffix(path)).setSize(IoUtils.size(FileUtil.getInputStream(path)))
                .setSuffix("." + FileUtil.getSuffix(path));

        String format = String.format("http://%s/basic-file/file/upload/start", host);
        Result<Object> result = OkHttpUtils.post(format, JSONUtil.toJsonStr(fileUpDto), Result.class);
        if (!result.validateOk()) {
            throw new RuntimeException(result.getMessage());
        }
        JSONObject entries = JSONUtil.toBean(JSONUtil.toJsonStr(result.getData()), JSONObject.class);
        System.err.println("start :" + JSONUtil.toJsonStr(entries, JSONConfig.create().setIgnoreNullValue(false)));
        //JSONObject data = entries.getByPath("data", JSONObject.class);
        int chunkSize = (Integer) entries.getByPath("chunkSize");
        int chunkNumber = (Integer) entries.getByPath("chunkNumber");
        int totalChunks = (Integer) entries.getByPath("totalChunks");
        Long totalFileSize = Long.parseLong("" + entries.getByPath("totalFileSize"));
        Long fileId = Long.valueOf((Integer) entries.getByPath("fileId"));
        String identifier = (String) entries.getByPath("identifier");

        InputStream inputStream = FileUtil.getInputStream(path);

        List<InputStream> streamList = IoUtils.splitInputStream(inputStream, chunkSize);

        String uploadUrl = String.format("http://%s/basic-file/file/upload/chunk", host);
        if (useExecutor) {
            uploadChunkExecutor(chunkNumber, totalChunks, totalFileSize, fileId, identifier, streamList, uploadUrl);
        } else {
            uploadChunk(chunkNumber, totalChunks, totalFileSize, fileId, identifier, streamList, uploadUrl);
        }
        String mergeUrl = String.format("http://%s/basic-file/file/upload/merge", host);
        merge(mergeUrl, fileName, identifier, totalChunks, fileId, totalFileSize);
    }

    private static void uploadChunk(int chunkNumber, int totalChunks, Long totalFileSize, Long fileId, String identifier, List<InputStream> streamList, String uploadUrl) throws Exception {
        for (InputStream input : streamList) {
            uploadChunk(uploadUrl, identifier, fileId, chunkNumber, totalChunks, totalFileSize, input);
            chunkNumber++;
        }
    }

    private static void uploadChunkExecutor(int chunkNumber, int totalChunks, Long totalFileSize, Long fileId, String identifier, List<InputStream> streamList, String uploadUrl) {
        log.info("uploadChunkExecutor start ...");
        //多线程执行
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        List<Future<?>> futures = CollUtil.newArrayList();
        if (chunkNumber > -1) {
            chunkNumber--;
        }
        for (InputStream input : streamList) {
            final int currentChunk = chunkNumber++; // 确保每个任务的 chunkNumber 唯一
            futures.add(executorService.submit(() -> {
                try {
                    log.debug("uploadChunkExecutor chunkNumber: {}", currentChunk);
                    uploadChunk(uploadUrl, identifier, fileId, currentChunk, totalChunks, totalFileSize, input);
                } catch (Exception e) {
                    log.error("上传 chunk:{},失败：{}", currentChunk, e.getMessage());
                    throw new RuntimeException(e);
                }
            }));
        }

        // 等待所有上传任务完成
        for (Future<?> future : futures) {
            try {
                future.get(); // 等待任务完成
            } catch (InterruptedException | ExecutionException e) {
                log.error("任务执行失败：{}", e.getMessage());
            }
        }

        // 关闭线程池
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.MINUTES)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }

    public static void uploadChunk(String url, String identifier, Long fileId, int chunkNumber, int totalChunks, Long totalFileSize, InputStream inputStream) throws Exception {
        log.debug("uploadChunk chunkNumber:{}", chunkNumber);
        // 创建 multipart 请求体
        MultipartBody.Builder multipartBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "filename",
                        RequestBody.create(MediaType.parse("application/octet-stream"), IoUtils.toByteArray(inputStream)))
                .addFormDataPart("chunkNumber", String.valueOf(chunkNumber))
                .addFormDataPart("totalChunks", String.valueOf(totalChunks))
                .addFormDataPart("identifier", String.valueOf(identifier))
                .addFormDataPart("totalFileSize", String.valueOf(totalFileSize))
                .addFormDataPart("fileId", String.valueOf(fileId));

        // 构建请求体
        RequestBody requestBody = multipartBuilder.build();
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.MINUTES)
                .readTimeout(60, TimeUnit.MINUTES)
                .build();
        String result = OkHttpUtils.sendHttpRequest(client, OkHttpUtils.HttpMethod.POST, url,null, requestBody, null);

        // 输出响应内容
        log.info("Response: {}", result);

/*
        // 使用 Hutool 创建 POST 请求，上传 multipart 表单数据
        HttpResponse response = HttpRequest.post(url)
                //.form(form)
                .form("file", IoUtils.toByteArray(inputStream), "file")
                .form("chunkNumber", chunkNumber)
                .form("totalChunks", totalChunks)
                .form("identifier", identifier)
                .form("totalFileSize", totalFileSize)
                .form("fileId", fileId)
                // 如果需要传 fileId 参数，可添加 .form("fileId", "12345")
                .execute();

        // 输出响应内容
        log.info("Response: {}", response.body());*/

    }

    public static void merge(String url, String fileName, String identifier, int totalChunks, Long fileId, Long totalFileSize) {
        log.info("Merging chunks...");
        Map<String, Object> params = new HashMap<>();
        params.put("fileName", fileName);
        params.put("identifier", identifier);
        params.put("totalChunks", totalChunks);
        params.put("fileId", fileId);
        params.put("totalFileSize", totalFileSize);

        //String json = OkHttpUtils.sendHttpRequest(null, OkHttpUtils.HttpMethod.GET, url, params,null, null);
        //OkHttpUtils.get(url, params);
        OkHttpClient client = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.MINUTES)
                .readTimeout(60, TimeUnit.MINUTES)
                .build();
        Result result = OkHttpUtils.post(client,url, params,null,null,null,Result.class);
        //OkHttpUtils.sendHttpRequest(null, OkHttpUtils.HttpMethod.POST, url, params,null,null,null);
  /*      HttpRequest request = HttpUtil.createPost(url);
        request.form(params);

        String result = request.execute().body();*/
        if (result.validateOk()) {
            log.info("Chunk identifier:{}, Merging. Server response:{}", identifier, result);
        } else {
            log.error("Chunk identifier:{}, Merging. Server response:{}", identifier, result);
        }
    }
}
