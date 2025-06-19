package com.actual.combat.basic.file.core.utils.http;

import cn.hutool.core.collection.CollUtil;
import com.actual.combat.basic.exceptions.BusinessException;
import com.actual.combat.basic.exceptions.GlobalCustomException;
import com.actual.combat.basic.result.Result;
import com.actual.combat.basic.utils.http.OkHttpUtils;
import com.google.common.collect.Maps;
import com.actual.combat.basic.file.core.config.FileUploadConfig;
import com.actual.combat.basic.file.core.exception.FileException;

import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author yan
 * @Date 2025/3/13 下午11:21:30
 * @Description
 */
public class FileHelper {
    public static List<byte[]> getBytesByRemote(String instanceId, String fileName, String folder, String identifier, Integer chunkNumber) {
        Map<String, Object> map = Maps.newLinkedHashMap();
        map.put("chunkNumber", chunkNumber);
        map.put("identifier", identifier);
        map.put("folder", folder);
        map.put("fileName", fileName);
        try {
            return getBytesByRemote(instanceId, map);
        } catch (FileException e) {
            throw new GlobalCustomException(e.getMessage());
        }
    }
    private static List<byte[]> getBytesByRemote(String instanceId, Map<String, Object> map) {
        List<byte[]> bytes = null;
        //远程获取
        String url = FileUploadConfig.getUrlByte(instanceId);
        Result<List<String>> result = OkHttpUtils.get(url, map, Result.class);
        if (result.validateOk()) {
            List<String> resultData = result.getData();
            if (CollUtil.isNotEmpty(resultData)) {
                Base64.Decoder decoder = Base64.getDecoder();
                bytes = resultData.stream().map(decoder::decode).collect(Collectors.toList());
            }else {
                throw new BusinessException("文件不存在");
            }
        }else {
            throw new FileException("系统繁忙");
        }
        return bytes;
    }
    public static boolean delBytesByRemote(String instanceId, String fileName, String folder, String identifier, Integer chunkNumber) {
        Map<String, Object> map = Maps.newLinkedHashMap();
        map.put("chunkNumber", chunkNumber);
        map.put("identifier", identifier);
        map.put("folder", folder);
        map.put("fileName", fileName);
        try {
            return delBytesByRemote(instanceId, map);
        } catch (FileException e) {
            throw new GlobalCustomException(e.getMessage());
        }
    }
    private static boolean delBytesByRemote(String instanceId, Map<String, Object> map) {
        //远程获取
        String url = FileUploadConfig.getUrlDel(instanceId);
        Result result = OkHttpUtils.delete(url, map, Result.class);
        if (!result.validateOk()) {
            throw new FileException("系统繁忙");
        }
        return true;
    }
}
