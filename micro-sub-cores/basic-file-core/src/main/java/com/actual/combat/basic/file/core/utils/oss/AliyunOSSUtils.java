package com.actual.combat.basic.file.core.utils.oss;

import cn.hutool.core.collection.CollUtil;
import com.actual.combat.basic.core.utils.io.IoUtils;
import com.actual.combat.basic.utils.object.ObjectUtils;
import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSErrorCode;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.Accessors;
import lombok.experimental.SuperBuilder;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

/**
 * @author yan
 * @date 2023/6/27 23:47
 */
public class AliyunOSSUtils {

    /**
     * 创造oss
     *
     * @param accessKeyId
     * @param accessKeySecret
     * @param endpoint
     * @return
     */
    public static OSS createOss(String accessKeyId, String accessKeySecret, String endpoint) {
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        return ossClient;
    }

    /**
     * Oss桶存在
     *
     * @param ossClient
     * @param bucketName
     * @return
     */
    public static boolean doesBucketExist(OSS ossClient, String bucketName) {
        return ossClient.doesBucketExist(bucketName);
    }

    /**
     * Oss创造桶
     *
     * @param ossClient
     * @param bucketName
     * @return
     */
    public static Bucket createBucketName(OSS ossClient, String bucketName) {
        Bucket bucket = null;
        //不存在就创造
        if (!doesBucketExist(ossClient, bucketName)) {
            bucket = ossClient.createBucket(bucketName);
        }
        return bucket;
    }

    /**
     * 获取桶信息
     *
     * @param ossClient
     * @param bucketName
     */
    public static BucketInfo lookBucket(OSS ossClient, String bucketName) {

        // 查看Bucket信息。详细请参看“SDK手册 > Java-SDK > 管理Bucket”。
        // 链接地址是：https://help.aliyun.com/document_detail/oss/sdk/java-sdk/manage_bucket.html?spm=5176.docoss/sdk/java-sdk/init
        BucketInfo info = ossClient.getBucketInfo(bucketName);
//        System.out.println("Bucket " + bucketName + "的信息如下：");
//        System.out.println("\t数据中心：" + info.getBucket().getLocation());
//        System.out.println("\t创建时间：" + info.getBucket().getCreationDate());
//        System.out.println("\t用户标志：" + info.getBucket().getOwner());
//
        return info;
    }


    /**
     * 获取桶中所有对象
     *
     * @param ossClient
     * @param bucketName
     * @return
     */
    public static List<OSSObjectSummary> lookBucketAllObject(OSS ossClient, String bucketName) {
        // 查看Bucket中的Object。详细请参看“SDK手册 > Java-SDK > 管理文件”。
        // 链接地址是：https://help.aliyun.com/document_detail/oss/sdk/java-sdk/manage_object.html?spm=5176.docoss/sdk/java-sdk/manage_bucket
        ObjectListing objectListing = ossClient.listObjects(bucketName);
        List<OSSObjectSummary> objectSummary = objectListing.getObjectSummaries();
//        System.out.println("您有以下Object：");
//        for (OSSObjectSummary object : objectSummary) {
//            System.out.println("\t" + object.getKey());
//        }
        return objectSummary;
    }

    /**
     * 上传文件
     *
     * @param ossClient
     * @param bucketName
     * @param flieName
     * @return 无时间期限的链接
     */
    @SneakyThrows
    public static String uploadOss(OSS ossClient, String bucketName, String flieName, InputStream input) {
        ossClient.putObject(bucketName, flieName, input);

        // 生成URL   expiration设置失效时间   1天
        Date expiration = new Date(System.currentTimeMillis() + 3600L * 1000 * 24);
        URL url = ossClient.generatePresignedUrl(bucketName, flieName, expiration);
        String ret = null;
        if (url != null) {
            ret = url.toString();
            //不对url后面的参数进行截取。否则链接后面的参数不生效。
            // 如：设置了链接失效时间 Expires=1649905689  访问链接时，第三方会根据Expires值来判断链接失效情况。
            ret = ret.substring(0, ret.indexOf("?"));

        }
        return ret;
    }

    public static String generateKey(OSS ossClient, String bucketName, String... extension) {
        String key;
        if (extension != null && !Arrays.asList(extension).isEmpty()) {
            key = UUID.randomUUID() + Arrays.stream(extension).findAny().get();
        } else {
            key = UUID.randomUUID() + "*";
        }
        try {
            ossClient.getObject(bucketName, key);
        } catch (OSSException e) {
            if (Objects.equals(OSSErrorCode.NO_SUCH_KEY, e.getErrorCode())) {
                return key;
            }
            throw e;
        }
        return generateKey(ossClient, bucketName);
    }


    /**
     * 文件分片上传
     * @param ossClient
     * @param bucketName
     * @param flieName
     * @param inputStream
     * @return
     */
    public static String uploadShardingOss(OSS ossClient, String bucketName, String flieName, InputStream inputStream) {
        String url = null;
        List<PartETag> partETags = null;
        try {
            String key = generateKey(ossClient, bucketName, flieName);
            InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, key);
            InitiateMultipartUploadResult result = ossClient.initiateMultipartUpload(request);
            String uploadId = result.getUploadId();
            final long partSize = 1 * 1024 * 1024L;
            byte[] toByteArray = IOUtils.toByteArray(inputStream);//1 MB。
            long fileLength = IoUtils.getInputStreamLength(toByteArray);
            int partCount = (int) (fileLength / partSize);
            if (fileLength % partSize != 0) {
                partCount++;
            }
            int finalPartCount = partCount;
//             创建线程池 执行分块上传。
            OSS finalOssClient = ossClient;
            CompletableFuture<List<PartETag>> supplyAsync = CompletableFuture.supplyAsync(() -> {
                List<PartETag> partETagArrayList = new ArrayList<>();
                try {
                    for (int i = 0; i < finalPartCount; i++) {
                        long startPos = i * partSize;
                        long curPartSize = (i + 1 == finalPartCount) ? (fileLength - startPos) : partSize;
                        UploadPartRequest uploadPartRequest = new UploadPartRequest();
                        uploadPartRequest.setBucketName(bucketName);
                        uploadPartRequest.setKey(key);
                        uploadPartRequest.setUploadId(uploadId);
                        // 设置上传的分片流。
                        // 以本地文件为例说明如何创建FIleInputstream，并通过InputStream.skip()方法跳过指定数据。
                        InputStream instream = new ByteArrayInputStream(toByteArray);
                        instream.skip(startPos);
                        uploadPartRequest.setInputStream(instream);
                        // 设置分片大小。除了最后一个分片没有大小限制，其他的分片最小为100 KB。
                        uploadPartRequest.setPartSize(curPartSize);
                        // 设置分片号。每一个上传的分片都有一个分片号，取值范围是1~10000，如果超出此范围，OSS将返回InvalidArgument错误码。
                        uploadPartRequest.setPartNumber(i + 1);
                        // 每个分片不需要按顺序上传，甚至可以在不同客户端上传，OSS会按照分片号排序组成完整的文件。
                        UploadPartResult uploadPartResult = finalOssClient.uploadPart(uploadPartRequest);
                        // 每次上传分片之后，OSS的返回结果包含PartETag。PartETag将被保存在partETags中。
                        partETagArrayList.add(uploadPartResult.getPartETag());
                    }
                } catch (IOException e) {
                    partETagArrayList = null;
                }
                return partETagArrayList;
            });
            supplyAsync.join();
            partETags = supplyAsync.get();

            if (partETags != null) {
                CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest(bucketName, key, uploadId, partETags);
                CompleteMultipartUploadResult completeMultipartUploadResult = ossClient.completeMultipartUpload(completeMultipartUploadRequest);

                url = completeMultipartUploadResult.getLocation();
            } else {
                throw new Exception("分片上传失败");
            }
            return url;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            ossClient.shutdown();
        }

    }

    /**
     * 上传文件
     * @param oss
     * @param bucketName
     * @param fileName
     * @param filePartSize
     * @param inputStream
     * @return
     */
    public static String uploadShardingOss(OSS oss, String bucketName, String fileName, Long filePartSize, InputStream inputStream) {
        String url = null;
        try {
            UploadShardInfo uploadShardInfo = getUploadShardInfo(oss, bucketName, fileName, inputStream, filePartSize);

            int partCount = uploadShardInfo.getPartCount();
            long partSize = uploadShardInfo.getPartSize();
            long bytesLength = uploadShardInfo.getBytesLength();
            String uploadId = uploadShardInfo.getUploadId();
            byte[] bytes = uploadShardInfo.getBytes();
            String key = uploadShardInfo.getKey();

            CompletableFuture<List<PartETag>> supplyAsync = CompletableFuture.supplyAsync(() -> {
                List<PartETag> partETags = CollUtil.newArrayList();
                try {
                    for (int index = 0; index < partCount; index++) {
                        long startSize = index * partSize;
                        long currentPartSize = (index + 1 == partCount) ? (bytesLength - startSize) : partSize;
                        UploadPartResult uploadPartResult = uploadPartCount(oss, bucketName, fileName, uploadId, bytes, index, startSize, currentPartSize);
                        partETags.add(uploadPartResult.getPartETag());
                    }
                } catch (IOException e) {
                    partETags = null;
                }
                return partETags;
            });
            supplyAsync.join();
            List<PartETag> partETags = supplyAsync.get();
            if (partETags != null) {
                CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest(bucketName, key, uploadId, partETags);
                CompleteMultipartUploadResult completeMultipartUploadResult = oss.completeMultipartUpload(completeMultipartUploadRequest);
                url = completeMultipartUploadResult.getLocation();
            } else {
                throw new Exception("分片上传失败");
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            oss.shutdown();
        }
        return url;
    }

    /**
     * 分片上传信息实体
     */
    @Data
    @SuperBuilder
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    static class UploadShardInfo {
        private String key;
        private String uploadId;
        private int partCount;
        private long partSize;
        private byte[] bytes;
        private long bytesLength;
    }

    /**
     * 获取分片上传信息
     *
     * @param oss
     * @param bucketName
     * @param flieName
     * @return
     */
    @SneakyThrows
    public static UploadShardInfo getUploadShardInfo(OSS oss, String bucketName, String flieName, InputStream inputStream, Long partSize) {
        String key = generateKey(oss, bucketName, flieName);
        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(bucketName, key);
        InitiateMultipartUploadResult result = oss.initiateMultipartUpload(request);
        String uploadId = result.getUploadId();
        partSize = ObjectUtils.defaultIfEmpty(partSize, 1 * 1024 * 1024L);
        byte[] toByteArray = IOUtils.toByteArray(inputStream);//1 MB。

        long bytesLength = IoUtils.getInputStreamLength(toByteArray);
        int partCount = (int) (bytesLength / partSize);
        if (bytesLength % partSize != 0) {
            partCount++;
        }

        return UploadShardInfo.builder().uploadId(uploadId).partCount(partCount).partSize(partSize).bytesLength(bytesLength).key(key).bytes(toByteArray).build();
    }


    /**
     * 分片上传
     *
     * @param oss
     * @param bucketName
     * @param key
     * @param uploadId
     * @param toByteArray
     * @param partNumber
     * @param startPos
     * @param partSize
     * @return
     * @throws IOException
     */
    public static UploadPartResult uploadPartCount(OSS oss, String bucketName, String key, String uploadId, byte[] toByteArray, int partNumber, long startPos, long partSize) throws IOException {
        UploadPartRequest uploadPartRequest = new UploadPartRequest();
        uploadPartRequest.setBucketName(bucketName);
        uploadPartRequest.setKey(key);
        uploadPartRequest.setUploadId(uploadId);
        // 设置上传的分片流。
        // 以本地文件为例说明如何创建FIleInputstream，并通过InputStream.skip()方法跳过指定数据。
        InputStream instream = new ByteArrayInputStream(toByteArray);
        instream.skip(startPos);
        uploadPartRequest.setInputStream(instream);
        // 设置分片大小。除了最后一个分片没有大小限制，其他的分片最小为100 KB。
        uploadPartRequest.setPartSize(partSize);
        // 设置分片号。每一个上传的分片都有一个分片号，取值范围是1~10000，如果超出此范围，OSS将返回InvalidArgument错误码。
        uploadPartRequest.setPartNumber(partNumber + 1);
        // 每个分片不需要按顺序上传，甚至可以在不同客户端上传，OSS会按照分片号排序组成完整的文件。
        UploadPartResult uploadPartResult = oss.uploadPart(uploadPartRequest);
        return uploadPartResult;
    }


    /**
     * 上传文件
     *
     * @param ossClient
     * @param bucketName
     * @param flieName
     * @param input
     * @param timelong   时间有效--毫秒值(有效截止时间-上传时间的差值)
     * @return 有时间期限的链接
     */
    public static String uploadOssSetTime(OSS ossClient, String bucketName, String flieName, InputStream input, long timelong) {
        ossClient.putObject(bucketName, flieName, input);

        // 生成URL   expiration设置失效时间   1天
        Date expiration = new Date(System.currentTimeMillis() + timelong);
        URL url = ossClient.generatePresignedUrl(bucketName, flieName, expiration);
        String ret = null;
        if (url != null) {
            ret = url.toString();
            //不对url后面的参数进行截取。否则链接后面的参数不生效。
            // 如：设置了链接失效时间 Expires=1649905689  访问链接时，第三方会根据Expires值来判断链接失效情况。
            //ret = ret.substring(0, ret.indexOf("?"));

        }
        return ret;
    }


    /**
     * 下载文件--获取流
     *
     * @param ossClient
     * @param bucketName
     * @param fileName
     * @return
     * @throws IOException
     */
    public static ByteArrayOutputStream downloadOssOut(OSS ossClient, String bucketName, String fileName) throws IOException {
        // 下载文件。详细请参看“SDK手册 > Java-SDK > 下载文件”。
        // 链接地址是：https://help.aliyun.com/document_detail/oss/sdk/java-sdk/download_object.html?spm=5176.docoss/sdk/java-sdk/manage_object
        InputStream inputStream = downloadOssInput(ossClient, bucketName, fileName);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        IoUtils.copy(inputStream, outputStream);
        //inputStreamToOutputStream(inputStream, outputStream);
        inputStream.close();
        return outputStream;
    }

    /**
     * 下载文件--获取流
     *
     * @param ossClient
     * @param bucketName
     * @param fileName
     * @return
     * @throws IOException
     */
    public static InputStream downloadOssInput(OSS ossClient, String bucketName, String fileName) throws IOException {
        // 下载文件。详细请参看“SDK手册 > Java-SDK > 下载文件”。
        // 链接地址是：https://help.aliyun.com/document_detail/oss/sdk/java-sdk/download_object.html?spm=5176.docoss/sdk/java-sdk/manage_object
        OSSObject ossObject = ossClient.getObject(bucketName, fileName);
        InputStream inputStream = ossObject.getObjectContent();
        return inputStream;
    }

    ///**
    // * 输入写入输出
    // *
    // * @param input
    // * @param output
    // * @throws IOException
    // */
    //private static void inputStreamToOutputStream(InputStream input, OutputStream output) throws IOException {
    //    byte[] buf = new byte[8192];
    //    int length;
    //    while ((length = input.read(buf)) > 0) {
    //        output.write(buf, 0, length);
    //    }
    //}


    /**
     * 删除文件
     *
     * @param ossClient
     * @param bucketName
     * @param fileName
     */
    public static void deleteOss(OSS ossClient, String bucketName, String fileName) {
        ossClient.deleteObject(bucketName, fileName);
    }

    @SneakyThrows
    public static void main(String[] args) {
        String prx = "https://";
        String endpoint = "oss-cn-beijing.aliyuncs.com";
        String accessKeySecret = "accessKeySecret";
        String accessKeyId = "accessKeyId";
        OSS oss = createOss(accessKeyId, accessKeySecret, prx + endpoint);
        File file = new File("D:\\test.png");
        InputStream inputStream = new FileInputStream(file);
        String flieName = file.getName();
        String bucketName = "bucketName";
        String shardingOss = uploadShardingOss(oss, bucketName, flieName, inputStream);
        System.err.println(shardingOss);
        //int i = shardingOss.indexOf() + 1;
        String s = prx + bucketName + "." + endpoint + "/";
        flieName = shardingOss.split(s)[1];
        oss = createOss(accessKeyId, accessKeySecret, prx + endpoint);
        deleteOss(oss, bucketName, flieName);

    }
}
