package com.actual.combat.basic.dept.core.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.extra.spring.SpringUtil;
import com.actual.combat.auth.service.AuthUserService;
import com.actual.combat.basic.core.constant.user.UserConstants;
import com.actual.combat.basic.exceptions.BusinessException;
import com.actual.combat.basic.utils.object.ObjectUtils;
import com.actual.combat.dept.domain.SysDept;
import com.actual.combat.dept.domain.SysDeptAncestor;
import com.actual.combat.dept.domain.SysUserDept;
import com.actual.combat.dept.mapper.SysDeptMapper;
import com.actual.combat.dept.mapper.SysUserDeptMapper;
import com.actual.combat.mp.aop.dataScope.DataScope;
import com.actual.combat.vo.dept.DeptTreeVo;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.google.common.collect.Maps;
import com.actual.combat.basic.dept.core.service.SysDeptAncestorService;
import com.actual.combat.basic.dept.core.service.SysUserDeptService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import java.util.stream.Collectors;

import com.actual.combat.basic.dept.core.service.SysDeptService;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SysDeptServiceImpl extends ServiceImpl<SysDeptMapper, SysDept> implements SysDeptService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBatch(List<SysDept> list) {
        return baseMapper.updateBatch(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBatchUseMultiQuery(List<SysDept> list) {
        return baseMapper.updateBatchUseMultiQuery(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int updateBatchSelective(List<SysDept> list) {
        return baseMapper.updateBatchSelective(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchInsert(List<SysDept> list) {
        return baseMapper.batchInsert(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int batchInsertSelectiveUseDefaultForNull(List<SysDept> list) {
        return baseMapper.batchInsertSelectiveUseDefaultForNull(list);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertOrUpdate(SysDept record) {
        return baseMapper.insertOrUpdate(record);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int insertOrUpdateSelective(SysDept record) {
        return baseMapper.insertOrUpdateSelective(record);
    }

    @Override
    @DataScope(deptAlias = "d")
    public List<SysDept> selectDeptList(SysDept dept) {
        return baseMapper.selectSysDeptList(dept);
    }

    @Override
    public void checkDeptDataScope(List<Long> deptIds) {
        for (Long deptId : deptIds) {
            checkDeptDataScope(deptId);
        }
    }

    @Override
    public void checkDeptDataScope(Long deptId) {
        boolean isAdmin = SpringUtil.getBean(AuthUserService.class).isAdmin();
        if (!isAdmin && ObjectUtils.isNotNull(deptId)) {
            SysDept dept = new SysDept();
            dept.setDeptId(deptId);
            List<SysDept> deptList = SpringUtil.getBean(getClass()).selectDeptList(dept);
            if (CollUtil.isEmpty(deptList)) {
                throw new BusinessException("没有权限访问部门数据！");
            }
        }
    }

    @Override
    public boolean hasChildByDeptIds(List<Long> deptIds) {
        LambdaQueryWrapper<SysDept> query = Wrappers.lambdaQuery(SysDept.class);
        query.eq(SysDept::getDelFlag, "0")
                .in(SysDept::getParentId, deptIds)
                .last(" limit 1 ");
        long count = count(query);
        return count > 0;
    }

    @Override
    public boolean checkDeptExistUser(List<Long> deptIds) {
        List<SysUserDept> sysUserDeptList = new ArrayList<>();
        List<SysUserDept> userDeptList = SpringUtil.getBean(SysUserDeptService.class)
                .getByIds(deptIds);
        sysUserDeptList = SpringUtil.getBean(SysUserDeptMapper.class)
                .selectBatchIds(userDeptList.stream().map(SysUserDept::getUserId).collect(Collectors.toList()));
        return CollUtil.isNotEmpty(sysUserDeptList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteByDeptIds(List<Long> deptIds) {
        LambdaQueryWrapper<SysDept> query = Wrappers.lambdaQuery(SysDept.class);
        query.eq(SysDept::getDelFlag, "2")
                .in(SysDept::getDeptId, deptIds);
        return remove(query);
    }

    @Override
    public boolean checkDeptNameUnique(SysDept dept) {
        Long deptId = dept.getDeptId();
        LambdaQueryWrapper<SysDept> query = Wrappers.lambdaQuery(SysDept.class);
        query.eq(SysDept::getDeptName, dept.getDeptName())
                .eq(SysDept::getParentId, dept.getParentId())
                .eq(SysDept::getDelFlag, "0")
                .last(" limit 1 ");
        SysDept info = getOne(query);
        if (ObjectUtils.isNotNull(info) && !ObjectUtils.equals(info.getDeptId(), deptId)) {
            return UserConstants.NOT_UNIQUE;
        }
        return UserConstants.UNIQUE;
    }

    @Override
    public int selectNormalChildrenDeptById(Long deptId) {
        List<SysDeptAncestor> sysDeptAncestors = SpringUtil.getBean(SysDeptAncestorService.class)
                .selectDeptAncestorListByAncestorDeptId(deptId);
        List<Long> deptIds = sysDeptAncestors.stream().map(SysDeptAncestor::getDeptId).collect(Collectors.toList());
        LambdaQueryWrapper<SysDept> query = Wrappers.lambdaQuery(SysDept.class);
        query.eq(SysDept::getDelFlag, "0")
                .eq(SysDept::getStatus, UserConstants.DEPT_NORMAL)
                .in(SysDept::getDeptId, deptIds);
        return (int) count(query);
    }

    @Override
    public List<DeptTreeVo> selectTree(List<Long> ids) {
        List<DeptTreeVo> deptTreeVos = baseMapper.selectTree(ids);
        return deptTreeVos;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateDept(SysDept dept) {
        Long deptId = dept.getDeptId();
        Long parentId = dept.getParentId();
        if (ObjectUtils.equals(deptId, parentId)) {
            throw new BusinessException("修改部门'" + dept.getDeptName() + "'失败，上级部门不能是自己");
        }
        SysDept sysDept = getById(deptId);
        SysDept parentDept = getById(parentId);

        if (ObjectUtils.isEmpty(sysDept) || ObjectUtils.isNotEmpty(parentId) && ObjectUtils.isEmpty(parentDept)) {
            throw new BusinessException("部门不存在");
        }
        SysDeptAncestorService deptAncestorService = SpringUtil.getBean(SysDeptAncestorService.class);
        List<Long> subDeptIds = deptAncestorService.selectSubDeptAncestorListByAncestorDeptParentId(deptId);

        if (subDeptIds.contains(parentId)) {
            throw new BusinessException("非法操作！父不可移到子级以下！");
        }

        boolean update = updateById(dept);

        if (ObjectUtils.isNotEmpty(parentId) && !ObjectUtils.equals(parentId, sysDept.getParentId())) {
            // 如果修改了上级部门，则需要更新子节点的上级部门
            Map<String, DeptTreeVo> treeMap = Maps.newLinkedHashMap();
            List<DeptTreeVo> deptTreeVos = baseMapper.selectTree(CollUtil.newArrayList(deptId));
            deptTreeVos.stream().forEach(deptTree -> {
                treeMap.put(deptTree.getId() + "#" + deptTree.getParentId(), deptTree);
            });
            //todo: 需要更新子节点的上级部门 注意事务
            deptAncestorService.updateByParentDeptId(deptId, treeMap);
        }
        List<SysDeptAncestor> deptAncestors = deptAncestorService.selectDeptAncestorListByAncestorDeptId(deptId);
        deptAncestors.stream().filter(deptAncestor -> !ObjectUtils.equals(deptAncestor.getDeptParentId(), deptId))
                .map(SysDeptAncestor::getDeptParentId).collect(Collectors.toList());
        if (ObjectUtils.equals(UserConstants.DEPT_NORMAL, sysDept.getStatus())
                && ObjectUtils.isNotEmpty(sysDept.getParentId())
                && CollUtil.isNotEmpty(deptAncestors)) {
            // 如果该部门是启用状态，则启用该部门的所有上级部门
            updateParentDeptStatusNormal(deptAncestors.stream().map(SysDeptAncestor::getDeptId).collect(Collectors.toList()));
        }
        return update;
    }

    @Transactional(rollbackFor = Exception.class)
    public boolean updateParentDeptStatusNormal(List<Long> deptIds) {
        if (CollUtil.isNotEmpty(deptIds)) {
            LambdaUpdateWrapper<SysDept> update = Wrappers.lambdaUpdate(SysDept.class);
            update.set(SysDept::getStatus, UserConstants.DEPT_NORMAL).in(SysDept::getDeptId, deptIds);
            return update(update);
        }
        return false;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean saveDept(SysDept dept) {
        SysDept info = getById(dept.getParentId());
        // 如果父节点不为正常状态,则不允许新增子节点
        if (!UserConstants.DEPT_NORMAL.equals(info.getStatus())) {
            throw new BusinessException("部门停用，不允许新增");
        }
        boolean save = save(dept);
        if (save) {
            Long deptId = dept.getDeptId();
            List<DeptTreeVo> deptTreeVos = selectTree(CollUtil.newArrayList(deptId));
            List<SysDeptAncestor> ancestors = deptTreeVos.stream().map(deptTree -> {
                SysDeptAncestor deptAncestor = new SysDeptAncestor()
                        .setDeptId(deptTree.getId())
                        .setDeptParentId(deptTree.getParentId())
                        .setLevel(deptTree.getLevel());
                return deptAncestor;
            }).collect(Collectors.toList());

            SysDeptAncestorService deptAncestorService = SpringUtil.getBean(SysDeptAncestorService.class);
            deptAncestorService.saveBatch(ancestors);
        }
        return save;
    }
}
