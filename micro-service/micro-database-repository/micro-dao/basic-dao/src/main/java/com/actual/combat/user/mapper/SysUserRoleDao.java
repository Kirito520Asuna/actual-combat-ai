package com.actual.combat.user.mapper;

import com.actual.combat.mp.abs.mapper.MpMapper;
import com.actual.combat.user.domain.SysUserRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author yan
 * @Date 2024/9/28 上午1:26:19
 * @Description
 */
@Mapper
public interface SysUserRoleDao extends MpMapper<SysUserRole> {
    int updateBatch(List<SysUserRole> list);

    int updateBatchSelective(List<SysUserRole> list);

    int batchInsert(@Param("list") List<SysUserRole> list);

    int batchInsertSelectiveUseDefaultForNull(@Param("list") List<SysUserRole> list);

    int insertOrUpdate(SysUserRole record);

    int insertOrUpdateSelective(SysUserRole record);
}