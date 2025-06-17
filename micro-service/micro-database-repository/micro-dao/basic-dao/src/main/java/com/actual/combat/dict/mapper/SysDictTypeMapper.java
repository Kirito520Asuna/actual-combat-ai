package com.actual.combat.dict.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.actual.combat.dict.domain.SysDictType;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysDictTypeMapper extends BaseMapper<SysDictType> {
    int updateBatch(@Param("list") List<SysDictType> list);

    int updateBatchUseMultiQuery(@Param("list") List<SysDictType> list);

    int updateBatchSelective(@Param("list") List<SysDictType> list);

    int batchInsert(@Param("list") List<SysDictType> list);

    int batchInsertSelectiveUseDefaultForNull(@Param("list") List<SysDictType> list);

    int insertOnDuplicateUpdate(SysDictType record);

    int insertOnDuplicateUpdateSelective(SysDictType record);

    /**
     * 校验字典类型称是否唯一
     *
     * @param dictType 字典类型
     * @return 结果
     */
    SysDictType checkDictTypeUnique(String dictType);

    /**
     * 根据条件分页查询字典类型
     *
     * @param dictType 字典类型信息
     * @return 字典类型集合信息
     */
    List<SysDictType> selectDictTypeList(SysDictType dictType);
}