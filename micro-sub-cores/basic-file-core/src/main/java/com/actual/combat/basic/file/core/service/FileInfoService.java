package com.actual.combat.basic.file.core.service;

import java.util.List;

import com.actual.combat.file.domain.FileInfo;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Author yan
 * @Date 2025/3/6 14:16:20
 * @Description
 */
public interface FileInfoService extends IService<FileInfo> {


    int updateBatch(List<FileInfo> list);

    int updateBatchUseMultiQuery(List<FileInfo> list);

    int updateBatchSelective(List<FileInfo> list);

    int batchInsert(List<FileInfo> list);

    int batchInsertSelectiveUseDefaultForNull(List<FileInfo> list);

    int deleteByPrimaryKeyIn(List<Long> list);

    int insertOnDuplicateUpdate(FileInfo record);

    int insertOnDuplicateUpdateSelective(FileInfo record);

    FileInfo getByPartCode(String partCode);

}
