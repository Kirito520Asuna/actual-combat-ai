package com.actual.combat.dept.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.actual.combat.dept.domain.SysDeptAncestor;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysDeptAncestorMapper extends BaseMapper<SysDeptAncestor> {
    int updateBatch(@Param("list") List<SysDeptAncestor> list);

    int updateBatchUseMultiQuery(@Param("list") List<SysDeptAncestor> list);

    int updateBatchSelective(@Param("list") List<SysDeptAncestor> list);

    int batchInsert(@Param("list") List<SysDeptAncestor> list);

    int batchInsertSelectiveUseDefaultForNull(@Param("list") List<SysDeptAncestor> list);

    int insertOrUpdate(SysDeptAncestor record);

    int insertOrUpdateSelective(SysDeptAncestor record);
}