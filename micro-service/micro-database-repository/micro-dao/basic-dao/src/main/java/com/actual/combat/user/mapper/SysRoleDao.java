package com.actual.combat.user.mapper;

import com.actual.combat.mp.abs.mapper.MpMapper;
import com.actual.combat.user.domain.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author yan
 * @Date 2024/9/28 上午1:26:19
 * @Description
 */
@Mapper
public interface SysRoleDao extends MpMapper<SysRole> {
    int updateBatch(List<SysRole> list);

    int updateBatchSelective(List<SysRole> list);

    int batchInsert(@Param("list") List<SysRole> list);

    int batchInsertSelectiveUseDefaultForNull(@Param("list") List<SysRole> list);

    int deleteByPrimaryKeyIn(List<Long> list);

    int insertOrUpdate(SysRole record);

    int insertOrUpdateSelective(SysRole record);
}