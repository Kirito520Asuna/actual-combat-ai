package com.actual.combat.basic.user.core.service;

import java.util.List;

import com.actual.combat.user.domain.SysRole;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Author minimalism
 * @Date 2024/9/28 上午1:26:19
 * @Description
 */
public interface SysRoleService extends IService<SysRole> {

    List<String> getRoleKeys(List<Long> rolesId);

    int updateBatch(List<SysRole> list);

    int updateBatchSelective(List<SysRole> list);

    int batchInsert(List<SysRole> list);

    int batchInsertSelectiveUseDefaultForNull(List<SysRole> list);

    int deleteByPrimaryKeyIn(List<Long> list);

    int insertOrUpdate(SysRole record);

    int insertOrUpdateSelective(SysRole record);

    SysRole getByUserId(Long userId);
}
