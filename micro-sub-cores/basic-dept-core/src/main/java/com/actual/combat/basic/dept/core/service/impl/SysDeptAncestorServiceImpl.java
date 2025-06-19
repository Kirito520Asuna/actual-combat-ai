package com.actual.combat.basic.dept.core.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.actual.combat.dept.domain.SysDeptAncestor;
import com.actual.combat.dept.mapper.SysDeptAncestorMapper;
import com.actual.combat.vo.dept.DeptTreeVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import org.springframework.stereotype.Service;

import java.util.*;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.List;
import java.util.stream.Collectors;

import com.actual.combat.basic.dept.core.service.SysDeptAncestorService;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SysDeptAncestorServiceImpl extends ServiceImpl<SysDeptAncestorMapper, SysDeptAncestor> implements SysDeptAncestorService {

    @Override
    public int updateBatch(List<SysDeptAncestor> list) {
        return baseMapper.updateBatch(list);
    }

    @Override
    public int updateBatchUseMultiQuery(List<SysDeptAncestor> list) {
        return baseMapper.updateBatchUseMultiQuery(list);
    }

    @Override
    public int updateBatchSelective(List<SysDeptAncestor> list) {
        return baseMapper.updateBatchSelective(list);
    }

    @Override
    public int batchInsert(List<SysDeptAncestor> list) {
        return baseMapper.batchInsert(list);
    }

    @Override
    public int batchInsertSelectiveUseDefaultForNull(List<SysDeptAncestor> list) {
        return baseMapper.batchInsertSelectiveUseDefaultForNull(list);
    }

    @Override
    public int insertOrUpdate(SysDeptAncestor record) {
        return baseMapper.insertOrUpdate(record);
    }

    @Override
    public int insertOrUpdateSelective(SysDeptAncestor record) {
        return baseMapper.insertOrUpdateSelective(record);
    }

    @Override
    public List<SysDeptAncestor> selectDeptAncestorList(SysDeptAncestor deptAncestor) {
        Long id = deptAncestor.getId();
        Long deptId = deptAncestor.getDeptId();
        Long deptParentId = deptAncestor.getDeptParentId();
        Long level = deptAncestor.getLevel();
        LambdaQueryWrapper<SysDeptAncestor> wrapper = Wrappers.lambdaQuery(SysDeptAncestor.class);
        wrapper.eq(id != null, SysDeptAncestor::getId, id)
                .eq(deptId != null, SysDeptAncestor::getDeptId, deptId)
                .eq(deptParentId != null, SysDeptAncestor::getDeptParentId, deptParentId)
                .eq(level != null, SysDeptAncestor::getLevel, level);

        return list(wrapper);
    }

    @Override
    public List<SysDeptAncestor> selectDeptAncestorListByAncestorDeptId(Long deptId) {
        LambdaQueryWrapper<SysDeptAncestor> query = Wrappers.lambdaQuery(SysDeptAncestor.class);
        query.eq(SysDeptAncestor::getDeptParentId, deptId);
        return list(query);
    }

    @Override
    public List<Long> selectSubDeptAncestorListByAncestorDeptParentId(Long deptId) {
        LambdaQueryWrapper<SysDeptAncestor> query = new QueryWrapper<SysDeptAncestor>()
                .select("DISTINCT " + SysDeptAncestor.COL_DEPT_ID)
                .lambda();
        query.eq(SysDeptAncestor::getDeptParentId, deptId)
                .ge(SysDeptAncestor::getLevel, 0);
        List<Long> collect = list(query).stream().map(SysDeptAncestor::getDeptId).collect(Collectors.toList());
        return collect;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateByParentDeptId(Long deptId, Map<String, DeptTreeVo> treeMap) {
        LambdaQueryWrapper<SysDeptAncestor> query = Wrappers.lambdaQuery(SysDeptAncestor.class);
        query.eq(SysDeptAncestor::getDeptParentId, deptId);
        List<SysDeptAncestor> list = list(query);
        Map<Long, List<SysDeptAncestor>> listMap = list.stream().collect(Collectors.groupingBy(SysDeptAncestor::getDeptId));

        List<SysDeptAncestor> saveList = CollUtil.newArrayList();
        List<SysDeptAncestor> updateList = CollUtil.newArrayList();
        List<Long> delList = CollUtil.newArrayList();

        for (Map.Entry<Long, List<SysDeptAncestor>> entry : listMap.entrySet()) {
            Long key = entry.getKey();
            List<SysDeptAncestor> values = entry.getValue();
            // todo: 判断是否需要删除 需要更新
            for (SysDeptAncestor deptAncestor : values) {
                Long id = deptAncestor.getDeptId();
                Long parentId = deptAncestor.getDeptParentId();
                DeptTreeVo deptTreeVo = treeMap.get(id + "#" + parentId);
                if (deptTreeVo != null) {
                    deptAncestor.setLevel(deptTreeVo.getLevel());
                    updateList.add(deptAncestor);
                } else {
                    //treeMap不存在的 需要删除
                    delList.add(deptAncestor.getId());
                }
            }
        }

        boolean ok = false;
        if (CollUtil.isNotEmpty(saveList)) {
            // 应该不存在这中情况 先保留
            saveBatch(saveList);
            ok = true;
        }
        if (CollUtil.isNotEmpty(updateList)) {
            updateBatch(updateList);
            ok = true;
        }
        if (CollUtil.isNotEmpty(delList)) {
            removeByIds(delList);
            ok = true;
        }
        return ok;
    }
}
