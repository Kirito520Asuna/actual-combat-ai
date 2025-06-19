package com.actual.combat.basic.user.core.service.impl;

import com.actual.combat.user.domain.SysMenuAncestor;
import com.actual.combat.user.mapper.SysMenuAncestorDao;
import org.springframework.stereotype.Service;

import java.util.List;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.Map;

import com.actual.combat.basic.user.core.service.SysMenuAncestorService;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Author minimalism
 * @Date 2024/9/28 上午1:26:20
 * @Description
 */
@Service
public class SysMenuAncestorServiceImpl extends ServiceImpl<SysMenuAncestorDao, SysMenuAncestor> implements SysMenuAncestorService{

    /**
     * @param hashMap 
     * @return
     */
    @Override
    public List<SysMenuAncestor> selectByCustomMap(Map<String, Object> hashMap) {
        return baseMapper.selectByCustomMap(hashMap);
    }

    /**
     * @param deleteMap 
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByCustomMap(Map<String, Object> deleteMap) {
        return baseMapper.deleteByCustomMap(deleteMap);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBatch(List<SysMenuAncestor> list) {
        return baseMapper.updateBatch(list);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBatchSelective(List<SysMenuAncestor> list) {
        return baseMapper.updateBatchSelective(list);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchInsert(List<SysMenuAncestor> list) {
        return baseMapper.batchInsert(list);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchInsertSelectiveUseDefaultForNull(List<SysMenuAncestor> list) {
        return baseMapper.batchInsertSelectiveUseDefaultForNull(list);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteByPrimaryKeyIn(List<Long> list) {
        return baseMapper.deleteByPrimaryKeyIn(list);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertOrUpdate(SysMenuAncestor record) {
        return baseMapper.insertOrUpdate(record);
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertOrUpdateSelective(SysMenuAncestor record) {
        return baseMapper.insertOrUpdateSelective(record);
    }
}
