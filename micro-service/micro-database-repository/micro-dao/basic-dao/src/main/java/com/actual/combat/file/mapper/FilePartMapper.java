package com.actual.combat.file.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.actual.combat.file.domain.FilePart;

import java.util.List;

import com.actual.combat.vo.PartVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author yan
 * @Date 2025/3/12 22:58:05
 * @Description
 */
@Mapper
public interface FilePartMapper extends BaseMapper<FilePart> {
    int updateBatch(@Param("list") List<FilePart> list);

    int updateBatchUseMultiQuery(@Param("list") List<FilePart> list);

    int updateBatchSelective(@Param("list") List<FilePart> list);

    int batchInsert(@Param("list") List<FilePart> list);

    int batchInsertSelectiveUseDefaultForNull(@Param("list") List<FilePart> list);

    int batchInsertOrUpdate(@Param("list") List<FilePart> list);

    int deleteByPrimaryKeyIn(List<Long> list);

    int insertOnDuplicateUpdate(FilePart record);

    int insertOnDuplicateUpdateSelective(FilePart record);

    List<PartVo> getPartList(@Param("identifier") String identifier, @Param("fileId") Long fileId);

    int deleteByFileId(@Param("identifier") String identifier, @Param("fileId") Long fileId);

    List<FilePart> getPartsByFileIdFirstPartCount(@Param("fileId") Long fileId, @Param("partCount") int partCount, @Param("excludeIds") List<Long> excludeIds);
}