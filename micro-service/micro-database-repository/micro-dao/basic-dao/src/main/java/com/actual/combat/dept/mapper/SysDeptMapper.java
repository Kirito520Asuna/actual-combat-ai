package com.actual.combat.dept.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.actual.combat.dept.domain.SysDept;
import java.util.List;

import com.actual.combat.vo.dept.DeptTreeVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SysDeptMapper extends BaseMapper<SysDept> {
    int updateBatch(@Param("list") List<SysDept> list);

    int updateBatchUseMultiQuery(@Param("list") List<SysDept> list);

    int updateBatchSelective(@Param("list") List<SysDept> list);

    int batchInsert(@Param("list") List<SysDept> list);

    int batchInsertSelectiveUseDefaultForNull(@Param("list") List<SysDept> list);

    int insertOrUpdate(SysDept record);

    int insertOrUpdateSelective(SysDept record);

    List<SysDept> selectSysDeptList(SysDept dept);

    List<DeptTreeVo> selectTree(@Param("ids") List<Long> ids);
}