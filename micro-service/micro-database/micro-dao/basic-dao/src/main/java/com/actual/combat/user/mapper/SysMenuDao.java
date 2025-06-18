package com.actual.combat.user.mapper;

import com.actual.combat.mp.abs.mapper.MpMapper;
import com.actual.combat.user.domain.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Author yan
 * @Date 2024/9/28 上午1:26:19
 * @Description
 */
@Mapper
public interface SysMenuDao extends MpMapper<SysMenu> {
    int updateBatch(List<SysMenu> list);

    int updateBatchSelective(List<SysMenu> list);

    int batchInsert(@Param("list") List<SysMenu> list);

    int batchInsertSelectiveUseDefaultForNull(@Param("list") List<SysMenu> list);

    int deleteByPrimaryKeyIn(List<Long> list);

    int insertOrUpdate(SysMenu record);

    int insertOrUpdateSelective(SysMenu record);

    int updateByMap(Map<String, Object> toMap);

    List<SysMenu> selectByCustomMap(Map<String, Object> map);
    List<SysMenu> selectMenuTreeByUserId(Long userId);
}