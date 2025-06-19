package com.actual.combat.basic.user.core.service;

import java.util.List;
import java.util.Map;

import com.actual.combat.user.domain.SysMenuAncestor;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @Author minimalism
 * @Date 2024/9/28 上午1:26:20
 * @Description
 */
public interface SysMenuAncestorService extends IService<SysMenuAncestor> {

    List<SysMenuAncestor> selectByCustomMap(Map<String, Object> hashMap);

    int updateBatch(List<SysMenuAncestor> list);

    int updateBatchSelective(List<SysMenuAncestor> list);

    int batchInsert(List<SysMenuAncestor> list);

    int batchInsertSelectiveUseDefaultForNull(List<SysMenuAncestor> list);

    int deleteByPrimaryKeyIn(List<Long> list);

    int insertOrUpdate(SysMenuAncestor record);

    int insertOrUpdateSelective(SysMenuAncestor record);

    int deleteByCustomMap(Map<String, Object> deleteMap);
}
