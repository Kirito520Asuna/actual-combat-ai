package com.actual.combat.dict.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.actual.combat.dict.domain.SysDictData;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysDictDataMapper extends BaseMapper<SysDictData> {
    int updateBatch(@Param("list") List<SysDictData> list);

    int updateBatchUseMultiQuery(@Param("list") List<SysDictData> list);

    int updateBatchSelective(@Param("list") List<SysDictData> list);

    int batchInsert(@Param("list") List<SysDictData> list);

    int batchInsertSelectiveUseDefaultForNull(@Param("list") List<SysDictData> list);

    int insertOnDuplicateUpdate(SysDictData record);

    int insertOnDuplicateUpdateSelective(SysDictData record);

    List<SysDictData> selectSysDictDataList(SysDictData sysDictData);

    List<SysDictData> selectDictDataListByType(String dictType);
}